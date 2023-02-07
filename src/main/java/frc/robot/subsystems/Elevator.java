// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Elevator extends SubsystemBase {
  /** Creates a new Elevator. */
  // Define the motor and encoders
  private final CANSparkMax m_motor;
  private final RelativeEncoder m_encoder;
  private final SparkMaxPIDController m_PIDController;


  // TODO: The following constants came from the 2022 robot.
  // These need to be set for this robot.  

  // Feedforward constants for the each Climber Arm
  private static final double ELEVATOR_KS = 0.182; // TODO: This may need to be tuned
  // The following constants are computed from https://www.reca.lc/arm
  private static final double ELEVATOR_KG = 1.19;
  private static final double ELEVATOR_KV = 7.67;
  private static final double ELEVATOR_KA = 0.19;

  // PID Constants for the Arm PID controller
  // Since we're using Trapeziodal control, all values will be 0 except for P
  private static final double ELEVATOR_K_P0 = 100;
  private static final double ELEVATOR_K_I = 0.0;
  private static final double ELEVATOR_K_D = 0.0;
  private static final double ELEVATOR_K_FF = 0.0;
  private static final double ELEVATOR_OFFSET_METER = Units.inchesToMeters(1.5);


  private final ElevatorFeedforward m_Feedforward = 
    new ElevatorFeedforward(ELEVATOR_KS, ELEVATOR_KG, ELEVATOR_KV, ELEVATOR_KA);

  private double m_kPElevator;
  private boolean m_resetElevatorPos = false;
  
  /** Creates a new Elevator. */
  public Elevator(CANSparkMax motor) {
    m_kPElevator = ELEVATOR_K_P0;

    // Create the motor, PID Controller and encoder.
    m_motor = motor;
    m_motor.restoreFactoryDefaults();

    m_PIDController = m_motor.getPIDController();
    m_PIDController.setP(m_kPElevator);
    m_PIDController.setI(ELEVATOR_K_I);
    m_PIDController.setD(ELEVATOR_K_D);
    m_PIDController.setFF(ELEVATOR_K_FF);

    m_encoder = m_motor.getEncoder();

    // Set the position conversion factor.
    m_encoder.setPositionConversionFactor((12.0 / 72.0) * Units.inchesToMeters((7.0/8.0) * Math.PI)); // was 5/8

    m_encoder.setPosition(ELEVATOR_OFFSET_METER);

    SmartDashboard.putNumber("elevator" + "/P Gain", m_kPElevator);
  }

  @Override
  public void periodic() {
    double encoderValue = m_encoder.getPosition();
    SmartDashboard.putNumber("elevator" + "/Encoder", Units.metersToInches(encoderValue));
    
    // update the PID val
    checkPIDVal();
  }

  protected void setSetPoint(TrapezoidProfile.State setPoint) {
    // Calculate the feedforward from the setPoint
    double feedforward = m_Feedforward.calculate(setPoint.position, setPoint.velocity);

    // Add the feedforward to the PID output to get the motor output
    // Remember that the encoder was already set to account for the gear ratios.

    if(m_resetElevatorPos){
      setPoint.position = m_encoder.getPosition();
      m_resetElevatorPos = false;
    }
    m_PIDController.setReference(setPoint.position, ControlType.kPosition, 0, feedforward / 12.0);
    SmartDashboard.putNumber("elevator" + "/setPoint", Units.metersToInches(setPoint.position));
  }

  private void checkPIDVal() {
    double p = SmartDashboard.getNumber("elevator" + "/P Gain", 0);
    // if PID coefficients on SmartDashboard have changed, write new values to controller
    if ((p != m_kPElevator)) {
      m_PIDController.setP(p);
      m_kPElevator = p;
    }
  }

  public double getExtent() {
    return m_encoder.getPosition();
  }

  public void resetExtent(){
    setSetPoint(new TrapezoidProfile.State(m_encoder.getPosition(), 0.0));
    m_resetElevatorPos = true;
  }

  public void setBrakeMode (boolean brake) {
    m_motor.setIdleMode(brake ? CANSparkMax.IdleMode.kBrake : CANSparkMax.IdleMode.kCoast);
  }
}
