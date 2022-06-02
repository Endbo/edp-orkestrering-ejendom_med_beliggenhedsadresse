@Library('ufst-pipeline-library@v0.7.4')
import dk.ufst.devops.branchnaming.Utils
import static dk.ufst.devops.branchnaming.Constants.*

properties([buildDiscarder(
                logRotator(artifactDaysToKeepStr: '5', artifactNumToKeepStr: '5', daysToKeepStr: '', numToKeepStr: '10')
            ), disableConcurrentBuilds(),
            [$class: 'GithubProjectProperty', displayName: '',
              projectUrlStr: 'https://github.ccta.dk/ejendomsdata/edp-ork-ejendom-med-beliggenhedsadresse/'],
            durabilityHint('MAX_SURVIVABILITY'),
            [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false]
          ])

def buildInfo = Artifactory.newBuildInfo()
def buildUtils

//Constants constants = new Constants()
private def pullRequestState = OPEN_PR_STATE
private final def codeIsReadyPR
private boolean cleanupTags = true

try {
    stage('Setup') {
        node {
            // Fetch build result from previous build
            safeCheckoutAndCleanup labels: [GITHUB_LABAL_KUBERNETES_SYNTAX_CHECKED]

            buildUtils = initializeConfig branchName: BRANCH_NAME
            buildInfo = Artifactory.newBuildInfo()

            stash excludes: 'target/**, .*/**', name: CODE_STASH_NAME
        }
        buildInfo.retention maxBuilds: buildUtils.getBuildRetention()
    }
    node {
        stage('Build Artifact') {
            def mavenBuildInfo = Artifactory.newBuildInfo()
            try{
                mavenBuildInfo = buildMavenTarget buildUtils: buildUtils,
                                                  goals: "package -Dmaven.test.skip -Dexec.skip -T1C -U",
                                                  opts: "-Xmx256m", env: "dev",
                                                  includePattern: "*-sources.jar"
                buildInfo.append mavenBuildInfo
            } catch (err) {
                if((buildUtils.isReleaseTag() || buildUtils.isReleaseCandidateTag()) && currentBuild.getPreviousBuild()) {
                    unstable 'Error when building Maven target. This might be fine as we get an error if artifact is already in artifactory. ' + err
                } else {
                    error err
                }
            }
            stash includes: 'target/*.jar', excludes: 'target/*.original', name: 'ARTIFACTS'
            stash includes: 'target/**/*', name: 'TARGET'
            logger message: "Current Build result: ${currentBuild.currentResult}"
        }
        stage('Test and Package') {
            parallel unitTesting: {
                unstash name: 'TARGET'
                mavenBuildInfoTesting = buildMavenTarget buildUtils: buildUtils,
                                                         goals: "test -Dmaven.main.skip",
                                                         env: "dev"
                stash includes: 'target/surefire-reports/**', name: 'UNIT-TEST'
                stash includes: 'target/jacoco.exec', name: 'JACOCO'
                buildInfo.append mavenBuildInfoTesting
                logger message: "Current Build result: ${currentBuild.currentResult}"
            }, buildingTheImage: {
                def mavenBuildInfoImage = Artifactory.newBuildInfo()
                mavenBuildInfoImage = buildMavenTarget buildUtils: buildUtils,
                                                       goals: "exec:exec@extract oc:build",
                                                       env: "dev"
                buildInfo.append mavenBuildInfoImage
                logger message: "Current Build result: ${currentBuild.currentResult}"
            }, lintDeployFile: {
                buildMavenTarget buildUtils: buildUtils,
                                 goals: "oc:resource@prepare-for-integration-test",
                                 opts: "-Xmx256m", env: "dev"
                // Let's validate the deployment YAML that we just generated
                docker.withRegistry('https://docker.artifactory.ccta.dk/', buildUtils.getArtifactoryCredentialdId()) {
                    def kubevalImage = docker.image('docker.artifactory.ccta.dk/garethr/kubeval:0.15.0');
                    kubevalImage.pull();
                    kubevalImage.inside("-v $WORKSPACE:/workspace --entrypoint=''") {
                         sh 'kubeval --openshift --skip-kinds Route -v 3.11.0 target/classes/META-INF/jkube/**/*.yml'
                    }
                }
                // The comment will only be created or updated if this is infact a PR build.
                addOrUpdatePRComment comment: "During build [${BUILD_NUMBER}](" + currentBuild.absoluteUrl +
                                              ") I just used the `kubeval` linter, to verify that all files matching \
                                               `artifacts/**/*.yml` are all correct.",
                                     msgGroup: "kubernetes",
                                     label: GITHUB_LABAL_KUBERNETES_SYNTAX_CHECKED
                logger message: "Current Build result: ${currentBuild.currentResult}"
            }
            failFast: false
            logger message: "Current Build result: ${currentBuild.currentResult}"
        }
    }
    if (buildUtils.isSonarQube()) {
        if(!(buildUtils.isFeatureBranch()) && !(buildUtils.isBugfixBranch())) {
            stage('Code Analysis') {
                node {
                    unstash name: 'JACOCO'
                    unstash name: 'TARGET'
                    withSonarQubeEnv(credentialsId: buildUtils.getSonarQubeCredentialsId(),
                                     installationName: buildUtils.getSonarQubeInstallationName()) {
                        echo "[DEBUG] SonarQube instance URL: ${env.SONAR_HOST_URL}"
                        def sonarqubeArguments = (env.CHANGE_ID) ?
                          "-Dsonar.scm.provider=git -Dsonar.pullrequest.key=${CHANGE_ID} \
                           -Dsonar.pullrequest.branch=${CHANGE_BRANCH} -Dsonar.pullrequest.base=${CHANGE_TARGET}" :
                          "-Dsonar.branch.name=${BRANCH_NAME}"
                        buildMavenTarget buildUtils: buildUtils,
                                         goals: "jacoco:check@default-check jacoco:report@default-report \
                                                 dependency-check:check \
                                                 sonar:sonar ${sonarqubeArguments} \
                                                 -U",
                                         env: "dev"
                        // Persist jacoco result in the jenkins build
                        jacoco()
                    } // submitted SonarQube taskId is automatically attached to the pipeline context
                }
                logger message: "Current Build result: ${currentBuild.currentResult}"
            }
            // No need to occupy a node
            stage("Quality Gate"){
                timeout(time: 1, unit: 'HOURS') {
                    // Just in case something goes wrong, pipeline will be killed after a timeout
                    def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
                    if (qg.status != 'OK') {
                        error "Pipeline aborted due to quality gate failure: ${qg.status}"
                    }
                }
                logger message: "Current Build result: ${currentBuild.currentResult}"
            }
        }
    }
    stage('Push Image') {
        node {
            def mavenBuildInfoPush = Artifactory.newBuildInfo()
            try{
                mavenBuildInfoPush = buildMavenTarget buildUtils: buildUtils,
                                                      goals: "oc:push",
                                                      env: "dev"
                buildInfo.append mavenBuildInfoPush
            } catch (err) {
                if((buildUtils.isReleaseTag() || buildUtils.isReleaseCandidateTag()) && currentBuild.getPreviousBuild()) {
                    unstable 'Error when pushing Maven target. This might be fine as we get an error if artifacts are already pushed to artifactory. ' + err
                } else {
                    error err
                }
            }
            logger message: "Current Build result: ${currentBuild.currentResult}"
        }
    }
    if(buildUtils.isPullRequest() || BRANCH_NAME == "development" || BRANCH_NAME == "test") {
        def deployEnv
        if(buildUtils.isPullRequest() || BRANCH_NAME == "development") deployEnv = "dev"
        else if(BRANCH_NAME == "test") deployEnv = "test"
        stage('Remove old deployment') {
            node {
                def mavenBuildInfo = Artifactory.newBuildInfo()
                mavenBuildInfo = buildMavenTarget buildUtils: buildUtils,
                        goals: "clean process-resources oc:resource@prepare-for-integration-test oc:undeploy",
                        env: deployEnv
            }
            logger message: "Current Build result: ${currentBuild.currentResult}"
        }
        stage('Deploy to ' + deployEnv) {
            def mavenBuildInfo = Artifactory.newBuildInfo()
            timestamps {
                node {
                    mavenBuildInfo = buildMavenTarget buildUtils: buildUtils,
                                                      goals: "clean process-resources \
                                                              oc:resource@prepare-for-integration-test \
                                                              oc:apply@prepare-for-integration-test",
                                                      env: deployEnv
                }

                // Find deployed service URL
                String deployedServiceUrl = buildUtils.calculateDeployedServiceUrl(deployEnv)
                def isServiceOnline = waitForServiceToComeOnline serviceBaseURL:deployedServiceUrl,
                                                                 healthcheckPath: buildUtils.getServiceHealthcheckPath()
                if (!isServiceOnline) {
                    throw new Exception("The service never responded on URL \
                                         ${deployedServiceUrl}/${buildUtils.getServiceHealthcheckPath()}")
                }
                logger message: "Current Build result: ${currentBuild.currentResult}"
            }
        }
        stage('Integration Test') {
            node {
                echo "Do integration tests here"
                stash allowEmpty: true, includes: 'target/surefire-reports/**', name: 'INTEGRATION-TESTS'
                logger message: "Current Build result: ${currentBuild.currentResult}"
            }
        }
    }

    if(buildUtils.isReleaseTag() || buildUtils.isReleaseCandidateTag()) {
        def environmentUrl = buildUtils.calculateDeployedServiceUrl("test")
        stage('Deploy to TST') {
            node {
                buildMavenTarget buildUtils: buildUtils, env: "test",
                                 goals: "clean process-resources oc:resource@prepare-for-integration-test \
                                         oc:apply@prepare-for-integration-test"

                String deployedServiceUrl = environmentUrl
                def isServiceOnline = waitForServiceToComeOnline serviceBaseURL:deployedServiceUrl,
                                                                 healthcheckPath: buildUtils.getServiceHealthcheckPath()
                if (!isServiceOnline) {
                    throw new Exception("The service never responded on URL \
                    ${deployedServiceUrl}/${buildUtils.getServiceHealthcheckPath()}")
                }
                logger message: "Current Build result: ${currentBuild.currentResult}"
            }
        }
        stage('Publish build info') {
            node {
                def server = Artifactory.server buildUtils.getArtifactoryServerId()
                unstash 'ARTIFACTS'
                server.publishBuildInfo buildInfo
                logger message: "Current Build result: ${currentBuild.currentResult}"
            }
        }
        if(buildUtils.isReleaseTag() || buildUtils.isReleaseCandidateTag()) {
            stage('Change log') {
                node {
                    generateChangelog buildUtils: buildUtils, method: MAVEN_METHOD
                    currentBuild.description = """
                    <b>Please review the release notes</b>
                    <br>
                    Read them here: <a href="${env.BUILD_URL}artifact/changelog.html">changelog.html</a>
                    """
                    logger message: "Current Build result: ${currentBuild.currentResult}"
                }
            }

            if (buildUtils.isReleaseTag()) {
                stage ('Mark for Prod') {
                    node {
                        buildMavenTarget buildUtils: buildUtils,
                                         args: "-Poverride-service-name -Dchangelist=",
                                         env: "prod",
                                         goals: "clean process-resources oc:resource@prepare-for-integration-test"
                        unstash 'CHANGELOG'
                        dir('flux') {
                            // Return a Pull Request JSON object
                            codeIsReadyPR = markTagReadyForProduction(
                                                                      buildUtils: buildUtils, name: 'flux',
                                                                      repository: buildUtils.getFluxCdRepoName(),
                                                                      changeLogFile: "../CHANGELOG.md",
                                                                      deploymentYaml: "../target/classes/META-INF/jkube/*.yml")
                            // TODO: Maybe it could make sense to store the PR info in a file -> to better ensure that this
                            //       part of the build handles Jenkins restarts
                            logger logLevel: LOG_LEVEL_INFO, message: "Pull Request created: " + codeIsReadyPR.number + \
                                 ". Take a closer look here: " + codeIsReadyPR.url
                            logger message: "Current Build result: ${currentBuild.currentResult}"
                        }
                    }
                }

                // TODO: Test what happens when jenkins is restarted during this waiting stage
                // TODO: create a new script for the below functionality
                stage('Wait for PR') {
                    // how many PR status checks
                    timestamps {
                        int prStatusChecks = 1
                        while (pullRequestState == OPEN_PR_STATE) {
                        logger message: "Currently at status check no: " + prStatusChecks
                            def prJson
                            node {
                                echo "[DEBUG] codeIsReadyPR.url: " + codeIsReadyPR.url
                                def rawJson = httpRequest acceptType: 'APPLICATION_JSON',
                                                          contentType: 'APPLICATION_JSON',
                                                          authentication: buildUtils.getGitHubCredentialsId(),
                                                          customHeaders: [[maskValue: false, name: 'Accept',
                                                                           value: 'application/vnd.github.v3+json']],
                                                          httpMode: 'GET',
                                                          url: codeIsReadyPR.url
                                prJson = readJSON text: rawJson.content
                            }

                            if (!(prJson.state) || prJson.state == CLOSED_PR_STATE) {
                                // Let's break the loop
                                pullRequestState = CLOSED_PR_STATE
                                break
                            } else {
                                // Let's sleep for a little while and then retry this operation
                            logger logLevel: LOG_LEVEL_INFO,
                                   message: "Sleeping for 1 minute, before rechecking the PR status."
                            sleep time: 1, unit: 'MINUTES'
                                prStatusChecks++
                            }
                        }
                        logger message: "Current Build result: ${currentBuild.currentResult}"
                    }
                }
            } else {
                try {
                    timeout(time: 7, unit: 'DAYS') {
                        stage('Cleanup') {
                            input message: 'Do you want to keep this deployment around \
                                            (will automatically expire within 7 days)?',
                                  ok: 'Yes. I want to keep this deployment.'
                            cleanupTags = false
                        }
                    }
                } catch (err) {
                    cleanupTags = true
                }
            }
            if (cleanupTags) {
                stage('Undeploy tags') {
                    logger logLevel: LOG_LEVEL_INFO,
                           message: "Tag deployment expired. The automated cleanup is now be commencing."
                    node {
                        if (pullRequestState == CLOSED_PR_STATE) {
                            commentOnPullRequest(buildUtils: buildUtils,
                                pullRequestNumber: codeIsReadyPR.number,
                                repository: buildUtils.getFluxCdRepoName(),
                                comment: "We will now undeploy (${BRANCH_NAME}) from DEV and TEST.")
                        }
                    }
                    parallel cleanupDev: {
                        stage('Undeploy Dev') {
                            node {
                                buildMavenTarget buildUtils: buildUtils, env: "dev",
                                goals: "clean process-resources oc:resource@prepare-for-integration-test oc:undeploy"
                            }
                        }
                    }, cleanupTest: {
                        stage('Undeploy Test') {
                            node {
                                buildMavenTarget buildUtils: buildUtils, env: "test",
                                goals: "clean process-resources oc:resource@prepare-for-integration-test oc:undeploy"
                            }
                        }
                    },
                    failFast: false
                }
            }
            if (buildUtils.isReleaseTag()) {
                stage('Is tag in PROD?') {
                    node {
                        String deployedServiceUrl = buildUtils.calculateDeployedServiceUrl("prod", "https", 443, true)

                        String projectVersion = getProjectVersion buildUtils: buildUtils
                        logger message: "projectVersion:" + projectVersion
                        // Let's calculated expacted info JSON
                        String serviceInfoJSON = '{"app":{"name":"' + buildUtils.getArtifactIdFromPom() + '","version":"' +
                               projectVersion + '"}}'
                        logger message: "serviceInfoJSON=" + serviceInfoJSON
                        def isServiceOnline = waitForServiceToComeOnline serviceBaseURL:deployedServiceUrl,
                                                                         healthcheckPath: 'actuator/info',
                                                                         validResponseContent: serviceInfoJSON
                        if (!isServiceOnline) {
                            throw new Exception("The service never responded on URL \
                            ${deployedServiceUrl}/${buildUtils.getServiceHealthcheckPath()}")
                        }
                    }
                }
            }
        }
    }
    stage('Reporting') {
        node {
            // We will fail the build if no unit tests exists.
            logger message: "Current Build result: ${currentBuild.currentResult}"
            if (currentBuild.currentResult != 'FAILURE') {
                try {
                    unstash 'UNIT-TEST'
                    logger message: "Unit tests in target: "
                    junit checksName: 'Unit Tests',
                    skipPublishingChecks: true,
                    testResults: 'target/surefire-reports/*.xml'
                } catch (e) {
                    logger logLevel: LOG_LEVEL_ERROR,
                    message: "Something went wrong during storing the unit test report: " + e
                    error 'No unit tests found! Failing the build.'
                }
                // We consider the build to be 'unstable' if we haven't run any integration tests.
                try {
                    if(!(buildUtils.isFeatureBranch() || buildUtils.isBugfixBranch())) {unstash 'INTEGRATION-TESTS'}
                } catch (e) {
                    unstable 'No integration tests found. Maybe we haven\'t run any.'
                }
                echo "[DEBUG] Current Build result: ${currentBuild.currentResult}"
            }
            logger message: "Current Build result: ${currentBuild.currentResult}"
        }
        if (buildUtils.isMainBranch()) {
            stage('Cleanup') {
                node {
                    cleanWs()
                }
            }
        }
    }
} catch (e) {
    error "Something bad happened. Stopping the build.\n" + e
}