<?php
include 'cong/confi.php';

$data = file_get_contents("php://input");
$request = json_decode($data);

$response = array();

if (isset($request->action)) {
    $action = $request->action;

    switch ($action) {

        case 'SEND_CONNECTION_REQUEST':
            $response = sendConnectionRequest($request);
            break;

        case 'ACCEPT_CONNECTION_REQUEST':
            $response = acceptConnectionRequest($request);
            break;
       case 'GET_ALL_JOB_SEEKERS':
            $response = getAllJobSeekers();
            break;

       case 'MY_JOBS':
            $response = getEmployerJobs($request);
            break;

       case 'GET_SIMILAR_JOBS':
            $response = getSimilarJobs($request);
            break; 

       case 'GET_ALL_JOBS':
            $response = getAllJobs($request);
            break;


       default:
            $response['status'] = false;
            $response['responseCode'] = 100;
            $response['message'] = "Invalid action specified";
    }
} else {
    $response['status'] = false;
    $response['responseCode'] = 100;
    $response['message'] = "Request action not defined";
}

echo json_encode($response);

function sendConnectionRequest($request)
{
    global $pdo;

    if (
        isset($request->action, $request->sender_id, $request->receiver_id)
        && $request->action === 'SEND_CONNECTION_REQUEST'
    ) {
        $senderId = $request->sender_id;
        $receiverId = $request->receiver_id;

        try {
            // Check if there is an existing pending connection request
            $existingRequestStmt = $pdo->prepare("SELECT * FROM connections WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) AND status = 'pending'");
            $existingRequestStmt->execute([$senderId, $receiverId, $receiverId, $senderId]);

            if ($existingRequestStmt->rowCount() > 0) {
                return [
                    'status' => false,
                    'responseCode' => 400,
                    'message' => "Connection request already exists",
                ];
            }

            // Attempt to insert the connection request into the 'connections' table
            $stmt = $pdo->prepare("INSERT INTO connections (sender_id, receiver_id, status, date_connected) VALUES (?, ?, 'pending', NOW())");
            $stmt->execute([$senderId, $receiverId]);

            return [
                'status' => true,
                'responseCode' => 200,
                'message' => "Connection request sent successfully",
            ];
        } catch (PDOException $e) {
            // Handle the exception (print or log the error, return an error response, etc.)
            return [
                'status' => false,
                'responseCode' => 500,
                'message' => "Error sending connection request: " . $e->getMessage(),
            ];
        }
    } else {
        return [
            'status' => false,
            'responseCode' => 400,
            'message' => "Invalid or missing parameters for connection request",
        ];
    }
}

function acceptConnectionRequest($request)
{
    global $pdo;

    if (isset($request->action, $request->connection_id) && $request->action === 'ACCEPT_CONNECTION_REQUEST') {
        $connectionId = $request->connection_id;

        try {
            // Update the status of the connection to 'accepted'
            $stmt = $pdo->prepare("UPDATE connections SET status = 'accepted' WHERE connection_id = ?");
            $stmt->execute([$connectionId]);

            return [
                'status' => true,
                'responseCode' => 200,
                'message' => "Connection request accepted successfully",
            ];
        } catch (PDOException $e) {
            // Handle the exception (print or log the error, return an error response, etc.)
            return [
                'status' => false,
                'responseCode' => 500,
                'message' => "Error accepting connection request: " . $e->getMessage(),
            ];
        }
    } else {
        return [
            'status' => false,
            'responseCode' => 400,
            'message' => "Invalid or missing parameters for connection acceptance",
        ];
    }
}
function getAllJobSeekers()
{
    global $pdo;

    try {
        $stmt = $pdo->prepare("SELECT username, field_of_interest, phone_no FROM job_seekers");
        $stmt->execute();

        $jobSeekers = $stmt->fetchAll(PDO::FETCH_ASSOC);

        return [
            'status' => true,
            'responseCode' => 200,
            'data' => $jobSeekers,
            'message' => "Job seekers' usernames, fields of interest, and phone numbers retrieved successfully",
        ];
    } catch (PDOException $e) {
        return [
            'status' => false,
            'responseCode' => 500,
            'message' => "Error retrieving job seekers: " . $e->getMessage(),
        ];
    }
}
function getEmployerJobs($request)
{
    global $pdo;

    if (isset($request->action, $request->employer_id) && $request->action === 'MY_JOBS') {
        $employerId = $request->employer_id;

        try {
            $stmt = $pdo->prepare("SELECT * FROM jobs WHERE employer_id = ?");
            $stmt->execute([$employerId]);

            $jobs = $stmt->fetchAll(PDO::FETCH_ASSOC);

            return [
                'status' => true,
                'responseCode' => 200,
                'jobs' => $jobs,
                'message' => "Employer's jobs retrieved successfully",
            ];
        } catch (PDOException $e) {
            return [
                'status' => false,
                'responseCode' => 500,
                'message' => "Error retrieving employer's jobs: " . $e->getMessage(),
            ];
        }
    } else {
        return [
            'status' => false,
            'responseCode' => 400,
            'message' => "Invalid or missing parameters for retrieving employer's jobs",
        ];
    }
}

function getSimilarJobs($request)
{
    global $pdo;

    if (
        isset($request->action, $request->job_seeker_id)
        && $request->action === 'GET_SIMILAR_JOBS'
    ) {
        $jobSeekerId = $request->job_seeker_id;

        try {
            // Fetch the job seeker's field of interest
            $fieldOfInterestStmt = $pdo->prepare("SELECT field_of_interest FROM job_seekers WHERE job_seeker_id = ?");
            $fieldOfInterestStmt->execute([$jobSeekerId]);
            $fieldOfInterestResult = $fieldOfInterestStmt->fetch(PDO::FETCH_ASSOC);

            if (!$fieldOfInterestResult || !$fieldOfInterestResult['field_of_interest']) {
                return [
                    'status' => false,
                    'responseCode' => 404,
                    'message' => 'Job seeker not found or field of interest not specified',
                    'data' => [],
                ];
            }

            $fieldOfInterest = json_decode($fieldOfInterestResult['field_of_interest']);

            // Fetch similar jobs based on the job seeker's field of interest
            $fieldString = implode(',', array_fill(0, count($fieldOfInterest), '?'));
            $similarJobsStmt = $pdo->prepare("
                SELECT * 
                FROM jobs 
                WHERE FIND_IN_SET(title, $fieldString)
            ");
            $similarJobsStmt->execute($fieldOfInterest);
            $similarJobs = $similarJobsStmt->fetchAll(PDO::FETCH_ASSOC);

            return [
                'status' => true,
                'responseCode' => 200,
                'data' => $similarJobs,
                'message' => 'Similar jobs retrieved successfully',
            ];
        } catch (PDOException $e) {
            return [
                'status' => false,
                'responseCode' => 500,
                'message' => 'Error retrieving similar jobs: ' . $e->getMessage(),
                'data' => [],
            ];
        }
    } else {
        return [
            'status' => false,
            'responseCode' => 400,
            'message' => 'Invalid or missing parameters for getting similar jobs',
            'data' => [],
        ];
    }
}   
function getAllJobs($request)
{
    global $pdo;

    if (
        isset($request->action)
        && $request->action === 'GET_ALL_JOBS'
    ) {
        try {
            // Fetch all fields including job_id and designation for all jobs
            $allJobsStmt = $pdo->prepare("SELECT job_id, title, location, description, salary, designation FROM jobs");
            $allJobsStmt->execute();
            $allJobs = $allJobsStmt->fetchAll(PDO::FETCH_ASSOC);

            return [
                'status' => true,
                'responseCode' => 200,
                'jobs' => $allJobs,
                'message' => 'All jobs retrieved successfully',
            ];
        } catch (PDOException $e) {
            return [
                'status' => false,
                'responseCode' => 500,
                'message' => 'Error retrieving all jobs: ' . $e->getMessage(),
                'jobs' => [],
            ];
        }
    } else {
        return [
            'status' => false,
            'responseCode' => 400,
            'message' => 'Invalid or missing parameters for getting all jobs',
            'jobs' => [],
        ];
    }
}




