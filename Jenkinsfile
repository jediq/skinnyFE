node {
    checkout scm
    sh 'chmod +x gradlew'
    sh './gradlew clean test jacoco sonarqube -Dsonar.scm.disabled=true'
}