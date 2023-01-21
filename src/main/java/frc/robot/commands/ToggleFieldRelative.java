// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.DriveTrain;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html

//this command changes back and forth between field centric and robot centric drive modes
public class ToggleFieldRelative extends InstantCommand {
    private final DriveTrain m_driveTrain;

    public ToggleFieldRelative(DriveTrain driveTrain) {
        m_driveTrain = driveTrain;
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        // toggle the field-centric mode flag
        m_driveTrain.toggleFieldCentric();
    }
}
