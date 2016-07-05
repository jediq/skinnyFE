node {
    stage 'Checkout'
    checkout scm

    stage 'Build'
    sh 'chmod +x gradlew'
    sh './gradlew clean test jacoco sonarqube -Dsonar.scm.disabled=true'
}