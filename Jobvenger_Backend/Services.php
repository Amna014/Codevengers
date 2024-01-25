<?php
include 'config/db_config.php';


$data = file_get_contents("php://input");
$request = json_decode($data);

$response = array();

if (isset($request->action)) {
    $action = $request->action;

    switch ($action) {
        case 'REGISTER_JOBSEEKER':
            $response = registerJobSeeker($request);
            break;

        case 'REGISTER_EMPLOYER':
            $response = registerEmployer($request);
            break;

        case 'CREATE_JOB':
            $response = createJob($request);
            break; 


        case 'APPLY_JOB':
            $response = applyJob($request);
            break;        
         
         case 'LOGIN':
            $response = loginUser($request);
            break; 

         case 'GET_APPLIED_USERS':
            $response = getAppliedUsersInMyJob($request);
            break;

         case 'GET_USER_BY_ID':
            $response = getUserById($request);
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

function registerEmployer($request)
{
    global $pdo;

    if (
        isset($request->action, $request->username, $request->email, $request->password)
        && $request->action === 'REGISTER_EMPLOYER'
    ) {
        $username = $request->username;
        $email = $request->email;
        $password = password_hash($request->password, PASSWORD_DEFAULT);
        $companyName = isset($request->company_name) ? $request->company_name : null;

        try {
            // Check if the email already exists
            $emailCheckStmt = $pdo->prepare("SELECT * FROM employers WHERE email = ?");
            $emailCheckStmt->execute([$email]);

            if ($emailCheckStmt->rowCount() > 0) {
                return [
                    'status' => false,
                    'responseCode' => 400,
                    'message' => "Email already registered. Please choose a different email address.",
                ];
            }

            // Attempt to insert the new employer into the 'employers' table
            $stmt = $pdo->prepare("INSERT INTO employers (username, email, password, company_name) VALUES (?, ?, ?, ?)");
            $stmt->execute([$username, $email, $password, $companyName]);

            return [
                'status' => true,
                'responseCode' => 200,
                'message' => "Employer registered successfully",
            ];
        } catch (PDOException $e) {
            // Handle the exception (print or log the error, return an error response, etc.)
            return [
                'status' => false,
                'responseCode' => 500,
                'message' => "Error registering employer: " . $e->getMessage(),
            ];
        }
    } else {
        return [
            'status' => false,
            'responseCode' => 400,
            'message' => "Invalid or missing parameters for employer registration",
        ];
    }
}

function registerJobSeeker($request)
{
    global $pdo;

    if (
        isset($request->action, $request->username, $request->email, $request->password)
        && $request->action === 'REGISTER_JOBSEEKER'
    ) {
        $username = $request->username;
        $email = $request->email;
        $password = password_hash($request->password, PASSWORD_DEFAULT);
        $fieldOfInterest = isset($request->field_of_interest) ? $request->field_of_interest : null;
        $phoneNo = isset($request->phone_no) ? $request->phone_no : null;

        try {
            // Check if the email already exists
            $emailCheckStmt = $pdo->prepare("SELECT * FROM job_seekers WHERE email = ?");
            $emailCheckStmt->execute([$email]);

            if ($emailCheckStmt->rowCount() > 0) {
                return [
                    'status' => false,
                    'responseCode' => 400,
                    'message' => "Email already registered. Please choose a different email address.",
                ];
            }

            // Attempt to insert the new job seeker into the 'job_seekers' table
            $stmt = $pdo->prepare("INSERT INTO job_seekers (username, email, password, field_of_interest, phone_no) VALUES (?, ?, ?, ?, ?)");
            $stmt->execute([$username, $email, $password, $fieldOfInterest, $phoneNo]);

            return [
                'status' => true,
                'responseCode' => 200,
                'message' => "Job seeker registered successfully",
            ];
        } catch (PDOException $e) {
            // Handle the exception (print or log the error, return an error response, etc.)
            return [
                'status' => false,
                'responseCode' => 500,
                'message' => "Error registering job seeker: " . $e->getMessage(),
            ];
        }
    } else {
        return [
            'status' => false,
            'responseCode' => 400,
            'message' => "Invalid or missing parameters for job seeker registration",
        ];
    }
}

function createJob($request)
{
    global $pdo;

    if (
        isset($request->action, $request->employer_id, $request->title, $request->designation, $request->experience_required, $request->description, $request->location, $request->salary)
        && $request->action === 'CREATE_JOB'
    ) {
        $employerId = $request->employer_id;
        $title = $request->title;
        $designation = $request->designation;
        $experienceRequired = $request->experience_required;
        $description = $request->description;
        $location = $request->location;
        $salary = $request->salary;

        try {
            // Attempt to insert the new job into the 'jobs' table
            $stmt = $pdo->prepare("INSERT INTO jobs (employer_id, title, designation, experience_required, description, location, salary) VALUES (?, ?, ?, ?, ?, ?, ?)");
            $stmt->execute([$employerId, $title, $designation, $experienceRequired, $description, $location, $salary]);

            return [
                'status' => true,
                'responseCode' => 200,
                'message' => "Job created successfully",
            ];
        } catch (PDOException $e) {
            // Handle the exception (print or log the error, return an error response, etc.)
            return [
                'status' => false,
                'responseCode' => 500,
                'message' => "Error creating job: " . $e->getMessage(),
            ];
        }
    } else {
        return [
            'status' => false,
            'responseCode' => 400,
            'message' => "Invalid or missing parameters for job creation",
        ];
    }
}




function applyJob($request)
{
    global $pdo;

    if (
        isset(
            $request->job_seeker_id,
            $request->job_id,
            $request->email,
            $request->phone_no,
            $request->experience,
            $request->expected_salary,
            $request->cv
        )
    ) {
        $jobSeekerId = $request->job_seeker_id;
        $jobId = $request->job_id;
        $email = $request->email;
        $phoneNo = $request->phone_no;
        $experience = $request->experience;
        $expectedSalary = $request->expected_salary;
        $cvPath = $request->cv;

        try {
            // Check if the job exists
            $jobStmt = $pdo->prepare("SELECT * FROM jobs WHERE job_id = ?");
            $jobStmt->execute([$jobId]);
            $job = $jobStmt->fetch(PDO::FETCH_ASSOC);

            if ($job) {
                // Check if the job seeker has already applied for the job
                $existingAppStmt = $pdo->prepare("SELECT * FROM applications WHERE job_seeker_id = ? AND job_id = ?");
                $existingAppStmt->execute([$jobSeekerId, $jobId]);
                $existingApplication = $existingAppStmt->fetch(PDO::FETCH_ASSOC);

                if ($existingApplication) {
                    return [
                        'status' => false,
                        'responseCode' => 400,
                        'message' => "Job seeker has already applied for this job",
                    ];
                }

                // Begin a transaction to ensure data consistency
                $pdo->beginTransaction();

                // Apply for the job
                $applyStmt = $pdo->prepare("INSERT INTO applications (job_id, job_seeker_id, email, phone_no, experience, expected_salary, cv) VALUES (?, ?, ?, ?, ?, ?, ?)");
                $applyStmt->execute([$jobId, $jobSeekerId, $email, $phoneNo, $experience, $expectedSalary, $cvPath]);

                // Update the applied_count column in the jobs table
                $updateAppliedCountStmt = $pdo->prepare("UPDATE jobs SET applied_count = applied_count + 1 WHERE job_id = ?");
                $updateAppliedCountStmt->execute([$jobId]);

                // Commit the transaction
                $pdo->commit();

                return [
                    'status' => true,
                    'responseCode' => 200,
                    'message' => "Job application submitted successfully",
                ];
            } else {
                return [
                    'status' => false,
                    'responseCode' => 404,
                    'message' => "Job not found",
                ];
            }
        } catch (PDOException $e) {
            // Rollback the transaction in case of an error
            $pdo->rollBack();

            return [
                'status' => false,
                'responseCode' => 500,
                'message' => "Error applying to the job: " . $e->getMessage(),
            ];
        }
    } else {
        return [
            'status' => false,
            'responseCode' => 400,
            'message' => "Missing required parameters for job application",
        ];
    }
}
function loginUser($request)
{
    global $pdo;

    if (
        isset($request->action, $request->email, $request->password)
        && $request->action === 'LOGIN'
    ) {
        $email = $request->email;
        $password = $request->password;

        try {
            // Check if the job seeker exists
            $jobSeekerStmt = $pdo->prepare("
                SELECT job_seeker_id as id, username, email, password, field_of_interest, phone_no, NULL as company_name
                FROM job_seekers 
                WHERE email = ?
            ");
            $jobSeekerStmt->execute([$email]);

            if ($jobSeekerStmt->rowCount() > 0) {
                $user = $jobSeekerStmt->fetch(PDO::FETCH_ASSOC);
                $userType = 'job_seeker';
            } else {
                // Check if the employer exists
                $employerStmt = $pdo->prepare("
                    SELECT employer_id as id, username, email, password, NULL as field_of_interest, NULL as phone_no, company_name
                    FROM employers 
                    WHERE email = ?
                ");
                $employerStmt->execute([$email]);

                if ($employerStmt->rowCount() > 0) {
                    $user = $employerStmt->fetch(PDO::FETCH_ASSOC);
                    $userType = 'employer';
                } else {
                    return [
                        'status' => false,
                        'responseCode' => 404,
                        'message' => 'User not found',
                    ];
                }
            }

            // Verify the password
            if (password_verify($password, $user['password'])) {
                // Remove sensitive data from the response
                unset($user['password']);

                // Add user type to the response
                $user['userType'] = $userType;

                return [
                    'status' => true,
                    'responseCode' => 200,
                    'message' => 'Login successful',
                    'user' => $user,
                ];
            } else {
                return [
                    'status' => false,
                    'responseCode' => 401,
                    'message' => 'Incorrect password',
                ];
            }
        } catch (PDOException $e) {
            return [
                'status' => false,
                'responseCode' => 500,
                'message' => 'Error during login: ' . $e->getMessage(),
            ];
        }
    } else {
        return [
            'status' => false,
            'responseCode' => 400,
            'message' => 'Invalid or missing parameters for login',
        ];
    }
}
function getAppliedUsersInMyJob($request)
{
    global $pdo;

    if (
        isset($request->action, $request->employer_id, $request->job_id)
        && $request->action === 'GET_APPLIED_USERS'
    ) {
        $employerId = $request->employer_id;
        $jobId = $request->job_id;

        try {
            // Check if the employer owns the job
            $ownershipStmt = $pdo->prepare("SELECT * FROM jobs WHERE employer_id = ? AND job_id = ?");
            $ownershipStmt->execute([$employerId, $jobId]);

            if ($ownershipStmt->rowCount() === 0) {
                return [
                    'status' => false,
                    'responseCode' => 403,
                    'message' => 'Permission denied. Employer does not own the specified job.',
                    'applied_users' => [],
                    'applied_users_count' => 0,
                ];
            }

            // Fetch applied count from the jobs table
            $appliedCountStmt = $pdo->prepare("SELECT applied_count FROM jobs WHERE job_id = ?");
            $appliedCountStmt->execute([$jobId]);
            $appliedCount = $appliedCountStmt->fetchColumn();

            // Fetch users who have applied to the specified job
            $appliedUsersStmt = $pdo->prepare("
                SELECT js.job_seeker_id, js.username, js.email, js.phone_no
                FROM job_seekers js
                JOIN applications app ON js.job_seeker_id = app.job_seeker_id
                WHERE app.job_id = ?
            ");
            $appliedUsersStmt->execute([$jobId]);
            $appliedUsers = $appliedUsersStmt->fetchAll(PDO::FETCH_ASSOC);

            // Count of users who have applied
            $appliedUsersCount = count($appliedUsers);

            return [
                'status' => true,
                'responseCode' => 200,
                'applied_users' => [
                    'users' => $appliedUsers,
                    'count' => $appliedUsersCount,
                ],
                'applied_users_count' => $appliedCount,
                'message' => 'Applied users for the specified job retrieved successfully',
            ];
        } catch (PDOException $e) {
            return [
                'status' => false,
                'responseCode' => 500,
                'message' => 'Error retrieving applied users: ' . $e->getMessage(),
                'applied_users' => [],
                'applied_users_count' => 0,
            ];
        }
    } else {
        return [
            'status' => false,
            'responseCode' => 400,
            'message' => 'Invalid or missing parameters for getting applied users',
            'applied_users' => [],
            'applied_users_count' => 0,
        ];
    }
}

function getUserById($request)
{
    global $pdo;

    if (
        isset($request->action, $request->user_id)
        && $request->action === 'GET_USER_BY_ID'
    ) {
        $userId = $request->user_id;

        try {
            // Check if the user is a job seeker
            $jobSeekerStmt = $pdo->prepare("
                SELECT job_seeker_id as id, username, email, field_of_interest, phone_no, NULL as company_name, NULL as designation
                FROM job_seekers 
                WHERE job_seeker_id = ?
            ");
            $jobSeekerStmt->execute([$userId]);

            if ($jobSeekerStmt->rowCount() > 0) {
                $user = $jobSeekerStmt->fetch(PDO::FETCH_ASSOC);
                $user['userType'] = 'job_seeker';
            } else {
                // Check if the user is an employer
                $employerStmt = $pdo->prepare("
                    SELECT employer_id as id, username, email, NULL as field_of_interest, NULL as phone_no, company_name, designation
                    FROM employers 
                    WHERE employer_id = ?
                ");
                $employerStmt->execute([$userId]);

                if ($employerStmt->rowCount() > 0) {
                    $user = $employerStmt->fetch(PDO::FETCH_ASSOC);
                    $user['userType'] = 'employer';
                } else {
                    return [
                        'status' => false,
                        'responseCode' => 404,
                        'message' => 'User not found',
                    ];
                }
            }

            return [
                'status' => true,
                'responseCode' => 200,
                'employee' => $user,
                'message' => 'User retrieved successfully',
            ];
        } catch (PDOException $e) {
            return [
                'status' => false,
                'responseCode' => 500,
                'message' => 'Error retrieving user: ' . $e->getMessage(),
            ];
        }
    } else {
        return [
            'status' => false,
            'responseCode' => 400,
            'message' => 'Invalid or missing parameters for getting user by ID',
        ];
    }
}



?>

