package frc.robot.commands;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;

import java.util.function.DoubleSupplier;

public class TurnToHeading extends CommandBase {
    private DriveTrain m_driveTrain;

    private final DoubleSupplier m_translationXSupplier;
    private final DoubleSupplier m_translationYSupplier;
    private final Rotation2d m_rotation2d;

    public TurnToHeading(DriveTrain driveTrain, DoubleSupplier translationXSupplier,
            DoubleSupplier translationYSupplier, Rotation2d rotation2d) {
        this.m_driveTrain = driveTrain;
        this.m_translationXSupplier = translationXSupplier;
        this.m_translationYSupplier = translationYSupplier;
        this.m_rotation2d = rotation2d;
        addRequirements(m_driveTrain);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void execute() {
        m_driveTrain.joystickDrive(m_translationXSupplier.getAsDouble(), m_translationYSupplier.getAsDouble(),
                m_rotation2d.getDegrees());

    }

    @Override
    public void end(boolean interrupted) {
        m_driveTrain.stop();
    }

}