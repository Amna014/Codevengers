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

        case 'GET_ALL_APPLIED_USERS':
            $response = getAllAppliedUsersInMyJobs($request);
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
            $existingRequestStmt = $pdo->prepare("SELECT * FROM connections WHERE (sender_id = ? AND receiver_id = ?) AND status = 'pending'");
            $existingRequestStmt->execute([$senderId, $receiverId]);

            if ($existingRequestStmt->rowCount() > 0) {
                // Automatically accept the connection request
                $acceptStmt = $pdo->prepare("UPDATE connections SET status = 'accepted', date_connected = NOW() WHERE sender_id = ? AND receiver_id = ? AND status = 'pending'");
                $acceptStmt->execute([$senderId, $receiverId]);

                return [
                    'status' => true,
                    'responseCode' => 200,
                    'message' => "Connection request accepted automatically",
                ];
            }

            // No existing request, send a new connection request
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
                'message' => "Error sending/accepting connection request: " . $e->getMessage(),
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

function getAllJobSeekers()
{
    global $pdo;

    try {
        // Fetch all job seekers along with their connection status
        $stmt = $pdo->prepare("
            SELECT js.job_seeker_id, js.username, js.field_of_interest, js.phone_no, c.status as connection_status
            FROM job_seekers js
            LEFT JOIN connections c ON js.job_seeker_id = c.sender_id
        ");
        $stmt->execute();

        $jobSeekers = $stmt->fetchAll(PDO::FETCH_ASSOC);

        return [
            'status' => true,
            'responseCode' => 200,
            'data' => $jobSeekers,
            'message' => "Job seekers' IDs, usernames, fields of interest, phone numbers, and connection status retrieved successfully",
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
        isset($request->action, $request->job_seeker_id, $request->field_of_interest)
        && $request->action === 'GET_SIMILAR_JOBS'
    ) {
        $jobSeekerId = $request->job_seeker_id;
        $fieldOfInterest = $request->field_of_interest;

        try {
            // Fetch similar jobs based on the job seeker's field of interest
            $fieldArray = explode(',', $fieldOfInterest);
            $fieldString = implode(',', array_fill(0, count($fieldArray), '?'));
            $similarJobsStmt = $pdo->prepare("
                SELECT * 
                FROM jobs 
                WHERE FIND_IN_SET(title, $fieldString)
            ");
            $similarJobsStmt->execute($fieldArray);
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
function getAllAppliedUsersInMyJobs($request)
{
    global $pdo;

    if (
        isset($request->action, $request->employer_id)
        && $request->action === 'GET_ALL_APPLIED_USERS'
    ) {
        $employerId = $request->employer_id;

        try {
            // Fetch all jobs owned by the employer
            $employerJobsStmt = $pdo->prepare("SELECT job_id FROM jobs WHERE employer_id = ?");
            $employerJobsStmt->execute([$employerId]);
            $employerJobs = $employerJobsStmt->fetchAll(PDO::FETCH_COLUMN);

            // Fetch users who have applied to the jobs owned by the employer along with connection status
            $appliedUsersStmt = $pdo->prepare("
                SELECT js.job_seeker_id, js.username, js.email, js.phone_no, c.status as connection_status
                FROM job_seekers js
                JOIN applications app ON js.job_seeker_id = app.job_seeker_id
                LEFT JOIN connections c ON js.job_seeker_id = c.sender_id
                WHERE app.job_id IN (" . implode(',', $employerJobs) . ")
            ");
            $appliedUsersStmt->execute();
            $appliedUsers = $appliedUsersStmt->fetchAll(PDO::FETCH_ASSOC);

            return [
                'status' => true,
                'responseCode' => 200,
                'data' => $appliedUsers,
                'message' => 'Applied users retrieved successfully',
            ];
        } catch (PDOException $e) {
            return [
                'status' => false,
                'responseCode' => 500,
                'message' => 'Error retrieving applied users: ' . $e->getMessage(),
                'data' => [],
            ];
        }
    } else {
        return [
            'status' => false,
            'responseCode' => 400,
            'message' => 'Invalid or missing parameters for getting applied users',
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
?>
