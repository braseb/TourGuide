pipeline {
    agent any
    stages {
        stage('Clean workspace') {
            steps {
                cleanWs() // Supprime les fichiers des builds précédents
            }
        }
                
        stage ('Install local library') {
            steps {
                dir('TourGuide') {
                    //Installation des jar locaux
                    sh 'mvn install:install-file -Dfile=libs/gpsUtil.jar -DgroupId=gpsUtil -DartifactId=gpsUtil -Dversion=1.0.0 -Dpackaging=jar'
                    sh 'mvn install:install-file -Dfile=libs/RewardCentral.jar -DgroupId=rewardCentral -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar'
                    sh 'mvn install:install-file -Dfile=libs/TripPricer.jar -DgroupId=tripPricer -DartifactId=tripPricer -Dversion=1.0.0 -Dpackaging=jar'
                }
            }
        }
         stage('Compile') {
            steps {
                echo "Compile"
                dir('TourGuide') {
                    // Run Maven on a Unix agent.
                    sh "mvn -DskipTests clean compile"

                    // To run Maven on a Windows agent, use
                    // bat "mvn -DskipTests clean compile"
                }
                
            }
        }
        stage('Launch tests'){
            steps{
                echo "Test"
                 dir('TourGuide') {
                     sh "mvn test"
                     junit stdioRetention: 'ALL', testResults: 'target/surefire-reports/*.xml'
                 }
               
            }
        }
        stage('Build the jar '){
           steps{
               echo "Build"
               dir('TourGuide') {
                   sh 'mvn -Dmaven.test.skip=true package'
               }
               
           }
            
        }
    } 
}

