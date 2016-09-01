node {
    stage 'Checkout'
    checkout scm
    sh 'chmod +x gradlew'

    stage 'Build'
    sh './gradlew clean test jacoco'

    stage 'SCA'
    sh './gradlew sonarqube -Dsonar.scm.disabled=true'
}
