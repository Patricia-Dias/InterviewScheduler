##################################
#           CADIDATES
##################################
curl -X 'POST' \
  'http://localhost:8083/api/scheduler/candidate/register' \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "Inês Santos",
  "email": "ines@gmail.com"
}'
curl -X 'POST' \
  'http://localhost:8083/api/scheduler/candidate/register' \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "Ana Carvalho",
  "email": "ana@gmail.com"
}'
curl -X 'POST' \
  'http://localhost:8083/api/scheduler/candidate/register' \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "Carlos Soares",
  "email": "soares@gmail.com"
}'
curl -X 'POST' \
  'http://localhost:8083/api/scheduler/candidate/register' \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "Xavier Mateus",
  "email": "xavier@gmail.com"
}'

##################################
#          INTERVIEWERS
##################################

curl -X 'POST' \
  'http://localhost:8083/api/scheduler/interviewer/register' \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "Ariana Grande",
  "email": "ariana@work.com"
}'
curl -X 'POST' \
  'http://localhost:8083/api/scheduler/interviewer/register' \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "Alfredo Martins",
  "email": "alfredo@work.com"
}'
curl -X 'POST' \
  'http://localhost:8083/api/scheduler/interviewer/register' \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "Rafaela Dias",
  "email": "rafaela@work.com"
}'
curl -X 'POST' \
  'http://localhost:8083/api/scheduler/interviewer/register' \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "Olivia Rodrigo",
  "email": "olivia@work.com"
}'


################################
#        INTERVIEW SLOTS
#################################

curl -X 'POST' 'http://localhost:8083/api/scheduler/interviewslot' \
  -H 'Accept: */*' -H 'Content-Type: application/json' \
  -d '{
  "time": "2022-09-01T10:00:00",
  "interviewer": {
    "name": "Olivia Rodrigo",
    "email": "olivia@work.com"
  }
}'

curl -X 'POST' 'http://localhost:8083/api/scheduler/interviewslot' \
  -H 'Accept: */*' -H 'Content-Type: application/json' \
  -d '{
  "time": "2022-09-01T11:00:00",
  "interviewer": {
    "name": "Olivia Rodrigo",
    "email": "olivia@work.com"
  }
}'

curl -X 'POST' 'http://localhost:8083/api/scheduler/interviewslot' \
  -H 'Accept: */*' -H 'Content-Type: application/json' \
  -d '{
  "time": "2022-09-01T11:00:00",
  "interviewer": {
    "name": "Ariana Grande",
    "email": "ariana@work.com"
  }
}'

curl -X 'POST' 'http://localhost:8083/api/scheduler/interviewslot' \
  -H 'Accept: */*' -H 'Content-Type: application/json' \
  -d '{
  "time": "2022-09-01T12:00:00",
  "interviewer": {
    "name": "Ariana Grande",
    "email": "ariana@work.com"
  }
}'

curl -X 'POST' 'http://localhost:8083/api/scheduler/interviewslot' \
  -H 'Accept: */*' -H 'Content-Type: application/json' \
  -d '{
  "time": "2022-09-01T10:00:00",
  "interviewer": {
    "name": "Ariana Grande",
    "email": "ariana@work.com"
  }
}'

curl -X 'POST' 'http://localhost:8083/api/scheduler/interviewslot' \
  -H 'Accept: */*' -H 'Content-Type: application/json' \
  -d '{
  "time": "2022-09-01T11:00:00",
  "interviewer": {
    "name": "Rafaela Dias",
    "email": "rafaela@work.com"
  }
}'

curl -X 'POST' 'http://localhost:8083/api/scheduler/interviewslot' \
  -H 'Accept: */*' -H 'Content-Type: application/json' \
  -d '{
  "time": "2022-09-02T12:00:00",
  "interviewer": {
    "name": "Rafaela Dias",
    "email": "rafaela@work.com"
  }
}'

curl -X 'POST' 'http://localhost:8083/api/scheduler/interviewslot' \
  -H 'Accept: */*' -H 'Content-Type: application/json' \
  -d '{
  "time": "2022-09-02T13:00:00",
  "interviewer": {
    "name": "Alfredo Martins",
    "email": "alfredo@work.com"
  }
}'




#################################
#   ASSIGN SLOTS TO CADIDATES
#################################
curl -X 'PUT' 'http://localhost:8083/api/scheduler/interviewslot?slotId=1&candidateId=1' \
  -H 'Accept: */*' -H 'Content-Type: application/json'

curl -X 'PUT' 'http://localhost:8083/api/scheduler/interviewslot?slotId=3&candidateId=3'  \
  -H 'Accept: */*' -H 'Content-Type: application/json' \

curl -X 'PUT' 'http://localhost:8083/api/scheduler/interviewslot?slotId=7&candidateId=4'  \
  -H 'Accept: */*' -H 'Content-Type: application/json' \