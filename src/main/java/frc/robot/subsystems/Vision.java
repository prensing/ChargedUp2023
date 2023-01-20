// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Vision extends SubsystemBase {

	private final PhotonCamera m_camera = new PhotonCamera("Cam");
	private final DriveTrain m_driveTrain;

	/** Creates a new Vision. */
	public Vision(DriveTrain driveTrain) {
		this.m_driveTrain = driveTrain;
	}

	@Override
	public void periodic() {
		// This method will be called once per scheduler run

		// System.out.println(m_camera.getDriverMode());

		var result = m_camera.getLatestResult();

		SmartDashboard.putBoolean("hasTargets?", result.hasTargets());
		SmartDashboard.putBoolean("drivemode", m_camera.getDriverMode());
		if (result.hasTargets()) {
			// Get the current best target.
			PhotonTrackedTarget target = result.getBestTarget();
			int targetID = target.getFiducialId();
			SmartDashboard.putNumber("targetID", targetID);

		} else {
			SmartDashboard.putNumber("targetID", -1);
			SmartDashboard.putNumber("PPL", m_camera.getPipelineIndex());
		}
	}

	public double getDistanceFromTarget(int targetGrid){ //an integer 1-9
		var result = m_camera.getLatestResult(); // camera's latest result
		if (result.hasTargets()) {
			// calculate range from target
			PhotonTrackedTarget target = result.getBestTarget(); //find best target
			int targetID = target.getFiducialId();
			SmartDashboard.putNumber("targetID", targetID);

			if(targetGrid == 1 || targetGrid == 4 || targetGrid == 7){ //if aiming to left of AprilTag

			} else if(targetGrid == 2 || targetGrid == 5 || targetGrid == 8){ //aiming towards middle
				//aim directly to distance
				return PhotonUtils.calculateDistanceToTargetMeters(
					Constants.CAMERA_HEIGHT_METERS,
					Constants.APRILTAG_TARGET_HEIGHT_METERS,
					Constants.CAMERA_PITCH_RADIANS,
					Units.degreesToRadians(result.getBestTarget().getPitch()));
			}else{ //aiming to right

			}


			
		}
		return 0.0; //if there is no target nothing happens
		
	}

	public double getYawFromTarget(){
		var result = m_camera.getLatestResult(); // camera's latest result
		if (result.hasTargets()) {
			return result.getBestTarget().getYaw(); //return yaw from target
			
		}
		return 0.0; //if no target
	}


	private Pose2d getPoseFromAprilTag(){
		AprilTagFieldLayout aprilTagFieldLayout = new ApriltagFieldLayout(AprilTagFieldLayout.loadFromResource(AprilTagFields.k2022RapidReact.m_resourceFile));
				//Forward Camera
		cam = new PhotonCamera("testCamera");
		Transform3d robotToCam = new Transform3d(new Translation3d(0.5, 0.0, 0.5), new Rotation3d(0,0,0)); //Cam mounted facing forward, half a meter forward of center, half a meter up from center.

		// Construct PhotonPoseEstimator
		PhotonPoseEstimator photonPoseEstimator = new PhotonPoseEstimator(aprilTagFieldLayout, PoseStrategy.CLOSEST_TO_REFERENCE_POSE, cam, robotToCam);
	}
	public Optional<EstimatedRobotPose> getEstimatedGlobalPose(Pose2d prevEstimatedRobotPose) {
        photonPoseEstimator.setReferencePose(prevEstimatedRobotPose);
        return photonPoseEstimator.update();
    }
	
}
