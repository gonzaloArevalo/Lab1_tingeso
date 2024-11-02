pipeline{
    agent any
    tools{
        maven "maven"

    }
    stages{
        stage("Build JAR File"){
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/gonzaloArevalo/Lab1_tingeso']])
                dir("Prestamo_backend"){
                    sh "mvn clean install"
                }
            }
        }
        stage("Test"){
            steps{
                dir("Prestamo_backend"){
                    sh "mvn test"
                }
            }
        }        
        stage("Build and Push Docker Image"){
            steps{
                dir("Prestamo_backend"){
                    script{
                         withDockerRegistry(credentialsId: 'docker-credentials'){
                            sh "docker build -t 746778/loan-backend:latest ."
                            sh "docker push 746778/loan-backend:latest"
                        }
                    }                    
                }
            }
        }
    }
}
