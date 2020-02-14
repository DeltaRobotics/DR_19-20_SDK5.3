package org.firstinspires.ftc.teamcode.robot.components;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class GenTwoBlockMover
{

    public Servo capstone;

    public  Servo foundation_mover;

    public Servo grabber_servo;

    public Servo auto_arm;

    public DcMotor blockArm;

    public Servo intake_left;

    public Servo intake_right;

    public Servo stone_pusher;

    public DcMotor lift;

    public int lift_level = 0;

    public boolean dpad_up_state = false;

    public static final double DEFAULT_ARM_POWER = 0.3;
    public static final double HOLD_POWER = 1.0; // Should ALWAYS be different from the other arm power variables
    public static final double PRESET_POSITION_POWER = 0.3;
    public static final int POSITION_DELTA = 15;
    public static final int GRAB_POSITION = 0;
    public static final int TRAVEL_POSITION = 250;
    public static final int DELIVER_POSITION = 950;


    public static final double GRABBER_OPEN = 0.9;
    public static final double GRABBER_INIT = 0.9;
    public static final double GRABBER_CLOSE = 0.45;
    public static final double FOUNDATION_MOVER_OPEN = 0.5;
    public static final double FOUNDATION_MOVER_CLOSE = 0.1;
    public static final double CAPSTONE_OPEN = 0.1;
    public static final double CAPSTONE_CLOSE = 0.99;
    public static final double STONE_PUSH = 1.0;
    public static final double STONE_PUSH_HOME = 0.22;
    public static final double AUTO_ARM_HOME = 0.93;
    public static final double AUTO_ARM_DOWN = 0.27;

    public static final int LIFT_POSITION_DELTA = 50;

    public static final double LIFT_PRESET_POWER = 1.0;

    public static final double LIFT_DEFAULT_POWER = 1.0;

    public static final int LIFT_MAX_POSITION = -6000;

    public static final int LIFT_MIN_POSITION = -200;
    public static final int LIFT_HOLD_POSITION = -1000;
    public static final double LIFT_HOLD_HIGH = 0.5;
    public static final double LIFT_HOLD_LOW = 0;

    public static final int[] LIFT_LEVEL_POSITIONS = new int[]{0, -2900, -4350, -5550, -5900};

    public LinearOpMode linearOpMode;

    // Constructor/Init
    public GenTwoBlockMover(LinearOpMode linearOpMode)
    {
        capstone = linearOpMode.hardwareMap.servo.get("capstone");

        foundation_mover = linearOpMode.hardwareMap.servo.get("foundationMover");

        grabber_servo = linearOpMode.hardwareMap.servo.get("grabber");

        intake_left = linearOpMode.hardwareMap.servo.get("intakeL");

        intake_right = linearOpMode.hardwareMap.servo.get("intakeR");

        blockArm = linearOpMode.hardwareMap.dcMotor.get("blockArm");

        lift = linearOpMode.hardwareMap.dcMotor.get("lift");

        stone_pusher = linearOpMode.hardwareMap.servo.get("stone_pusher");

        //auto_arm = linearOpMode.hardwareMap.servo.get("auto_arm");

        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        lift.setTargetPosition(lift.getCurrentPosition());

        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        lift.setPower(LIFT_DEFAULT_POWER);

        blockArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        blockArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        blockArm.setTargetPosition(blockArm.getCurrentPosition());

        blockArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        blockArm.setPower(1.0);

        grabber_servo.setPosition(GRABBER_INIT);

        foundation_mover.setPosition(FOUNDATION_MOVER_OPEN);

        capstone.setPosition(CAPSTONE_OPEN);



        stone_push_home();

        this.linearOpMode = linearOpMode;

        intake_Stop();

    }


    public void armControl(MecanumDriveTrain drive, double speed)
    {

        if(linearOpMode.gamepad2.dpad_up && !dpad_up_state && lift_level < (LIFT_LEVEL_POSITIONS.length - 1))
        {
            dpad_up_state = true;

            lift_level += 1;

        }
        else if(!linearOpMode.gamepad2.dpad_up)
        {
            dpad_up_state = false;
        }

        if(linearOpMode.gamepad2.back)
        {
            closeGrabber();

            if(blockArm.getCurrentPosition() < TRAVEL_POSITION)
            {
                moveArm(TRAVEL_POSITION, 0.3, 5, "Moving arm to travel", linearOpMode.telemetry);
            }

            if((lift.getTargetPosition() > LIFT_LEVEL_POSITIONS[lift_level]))
                {
                    lift.setTargetPosition(LIFT_LEVEL_POSITIONS[lift_level]);
                    lift.setPower(LIFT_PRESET_POWER);


                while(lift.isBusy() && !linearOpMode.gamepad2.dpad_down && lift.getCurrentPosition() > LIFT_MAX_POSITION)
                {

                    if (linearOpMode.gamepad1.left_trigger > 0.2)
                    {

                        speed = 1.0;
                    }
                    else
                    {

                        speed = 0.4;

                    }

                    //sets motor power according to joystick input
                    drive.motorRF.setPower(speed *drive.teleOpDrive(linearOpMode.gamepad1.right_stick_x, linearOpMode.gamepad1.right_stick_y, linearOpMode.gamepad1.left_stick_x)[0]);
                    drive.motorRB.setPower(speed *drive.teleOpDrive(linearOpMode.gamepad1.right_stick_x, linearOpMode.gamepad1.right_stick_y, linearOpMode.gamepad1.left_stick_x)[1]);
                    drive.motorLB.setPower(speed *drive.teleOpDrive(linearOpMode.gamepad1.right_stick_x, linearOpMode.gamepad1.right_stick_y, linearOpMode.gamepad1.left_stick_x)[2]);
                    drive.motorLF.setPower(speed *drive.teleOpDrive(linearOpMode.gamepad1.right_stick_x, linearOpMode.gamepad1.right_stick_y, linearOpMode.gamepad1.left_stick_x)[3]);

                    //robot.blockMover.blockArm.setPower(gamepad2.left_stick_y);


                    if (linearOpMode.gamepad1.left_trigger > 0.2)
                    {

                        speed = 1.0;
                    }
                    else
                    {

                        speed = 0.4;

                    }

                    if (linearOpMode.gamepad2.a)
                    {
                        openGrabber();
                    }

                    if (linearOpMode.gamepad2.b)
                    {
                        closeGrabber();
                    }

                    if(linearOpMode.gamepad1.left_bumper)
                    {
                        foundationDown();
                    }

                    if(linearOpMode.gamepad1.right_bumper)
                    {
                        foundationUp();
                    }

                    if (linearOpMode.gamepad1.right_trigger > 0.2)
                    {
                        intake_In();
                    }
                    else
                    {

                        intake_Stop();

                    }

                    if (linearOpMode.gamepad1.y)
                    {
                        intake_Out();
                    }


                    if (linearOpMode.gamepad2.dpad_left)
                    {
                        capstone_on();
                    }

                    if (linearOpMode.gamepad2.dpad_right)
                    {
                        capstone_off();
                    }

                    linearOpMode.telemetry.addData("Moving to lift level " + lift_level + ". At position: ", lift.getCurrentPosition());
                    linearOpMode.telemetry.update();
                }
            }


                lift_level = 0;

        }

        if(linearOpMode.gamepad2.dpad_down)
        {
            lift_level = 0;
        }




        if (linearOpMode.gamepad2.left_stick_y == 0 && blockArm.getPower() != HOLD_POWER && !blockArm.isBusy())
        {
            blockArm.setPower(HOLD_POWER);
        }
        else if (linearOpMode.gamepad2.left_stick_y < 0)
        {
            blockArm.setTargetPosition(blockArm.getTargetPosition() - POSITION_DELTA);
            blockArm.setPower(DEFAULT_ARM_POWER);
        } else if (linearOpMode.gamepad2.left_stick_y > 0)
        {
            blockArm.setTargetPosition(blockArm.getTargetPosition() + POSITION_DELTA);
            blockArm.setPower(DEFAULT_ARM_POWER);
        }

        if (linearOpMode.gamepad2.right_trigger > 0.5)
        {
            openGrabber();

            blockArm.setPower(PRESET_POSITION_POWER);
            blockArm.setTargetPosition(GRAB_POSITION);
        }

        if (linearOpMode.gamepad2.left_trigger > 0.5)
        {
            closeGrabber();

            blockArm.setPower(PRESET_POSITION_POWER);
            blockArm.setTargetPosition(DELIVER_POSITION);
        }

        if (linearOpMode.gamepad2.right_bumper) // Travel position
        {
            blockArm.setPower(PRESET_POSITION_POWER);

            openGrabber();

            blockArm.setTargetPosition(TRAVEL_POSITION);
            while (blockArm.isBusy() && !linearOpMode.gamepad2.dpad_down) {

                //sets motor power according to joystick input
               drive.motorRF.setPower(speed *drive.teleOpDrive(linearOpMode.gamepad1.right_stick_x, linearOpMode.gamepad1.right_stick_y, linearOpMode.gamepad1.left_stick_x)[0]);
               drive.motorRB.setPower(speed *drive.teleOpDrive(linearOpMode.gamepad1.right_stick_x, linearOpMode.gamepad1.right_stick_y, linearOpMode.gamepad1.left_stick_x)[1]);
               drive.motorLB.setPower(speed *drive.teleOpDrive(linearOpMode.gamepad1.right_stick_x, linearOpMode.gamepad1.right_stick_y, linearOpMode.gamepad1.left_stick_x)[2]);
               drive.motorLF.setPower(speed *drive.teleOpDrive(linearOpMode.gamepad1.right_stick_x, linearOpMode.gamepad1.right_stick_y, linearOpMode.gamepad1.left_stick_x)[3]);

                //robot.blockMover.blockArm.setPower(gamepad2.left_stick_y);


                if (linearOpMode.gamepad1.left_trigger > 0.2)
                {

                    speed = 1.0;
                }
                else
                {

                    speed = 0.4;

                }

                if (linearOpMode.gamepad2.a)
                {
                    openGrabber();
                }

                if (linearOpMode.gamepad2.b)
                {
                    closeGrabber();
                }

                if(linearOpMode.gamepad1.left_bumper)
                {
                    foundationDown();
                }

                if(linearOpMode.gamepad1.right_bumper)
                {
                    foundationUp();
                }

                if (linearOpMode.gamepad1.right_trigger > 0.2)
                {
                    intake_In();
                }
                else
                {

                    intake_Stop();

                }

                if (linearOpMode.gamepad1.y)
                {
                   intake_Out();
                }


                if (linearOpMode.gamepad2.dpad_left)
                {
                    capstone_on();
                }

                if (linearOpMode.gamepad2.dpad_right)
                {
                    capstone_off();
                }
            }

            lift.setTargetPosition(-50);
            lift.setPower(PRESET_POSITION_POWER);

            while (lift.isBusy() && !linearOpMode.gamepad2.dpad_down)
            {

                if (linearOpMode.gamepad1.left_trigger > 0.2)
                {

                    speed = 1.0;
                }
                else
                {

                    speed = 0.4;

                }

                if (linearOpMode.gamepad1.left_trigger > 0.2)
                {

                    speed = 1.0;
                }
                else
                {

                    speed = 0.4;

                }

                //sets motor power according to joystick input
                //sets motor power according to joystick input
                drive.motorRF.setPower(speed *drive.teleOpDrive(linearOpMode.gamepad1.right_stick_x, linearOpMode.gamepad1.right_stick_y, linearOpMode.gamepad1.left_stick_x)[0]);
                drive.motorRB.setPower(speed *drive.teleOpDrive(linearOpMode.gamepad1.right_stick_x, linearOpMode.gamepad1.right_stick_y, linearOpMode.gamepad1.left_stick_x)[1]);
                drive.motorLB.setPower(speed *drive.teleOpDrive(linearOpMode.gamepad1.right_stick_x, linearOpMode.gamepad1.right_stick_y, linearOpMode.gamepad1.left_stick_x)[2]);
                drive.motorLF.setPower(speed *drive.teleOpDrive(linearOpMode.gamepad1.right_stick_x, linearOpMode.gamepad1.right_stick_y, linearOpMode.gamepad1.left_stick_x)[3]);


                if (linearOpMode.gamepad2.a)
                {
                    openGrabber();
                }

                if (linearOpMode.gamepad2.b)
                {
                    closeGrabber();
                }

                if(linearOpMode.gamepad1.left_bumper)
                {
                    foundationDown();
                }

                if(linearOpMode.gamepad1.right_bumper)
                {
                    foundationUp();
                }

                if (linearOpMode.gamepad1.right_trigger > 0.2)
                {
                    intake_In();
                }
                else
                {

                    intake_Stop();

                }

                if (linearOpMode.gamepad1.y)
                {
                    intake_Out();
                }


                if (linearOpMode.gamepad2.dpad_left)
                {
                    capstone_on();
                }

                if (linearOpMode.gamepad2.dpad_right)
                {
                    capstone_off();
                }
            }

        }


        // Lift control
        // Moves lift up
        if (linearOpMode.gamepad2.right_stick_y < 0 &&  (lift.getCurrentPosition() >= LIFT_MAX_POSITION // Safety
                || linearOpMode.gamepad2.left_bumper)) // Override
        {
            if(lift.getPower() != LIFT_DEFAULT_POWER)
            {
                lift.setPower(LIFT_DEFAULT_POWER);
            }

            lift.setTargetPosition(lift.getTargetPosition() - LIFT_POSITION_DELTA);
            //lift.setPower(LIFT_DEFAULT_POWER);
        }
        // Moves lift down
        else if (linearOpMode.gamepad2.right_stick_y > 0 &&  (lift.getCurrentPosition() <= LIFT_MIN_POSITION // Safety
                || linearOpMode.gamepad2.left_bumper)) // Override
        {
            if(lift.getPower() != LIFT_DEFAULT_POWER)
            {
                lift.setPower(LIFT_DEFAULT_POWER);
            }

            lift.setTargetPosition(lift.getTargetPosition() + LIFT_POSITION_DELTA);
            //lift.setPower(LIFT_DEFAULT_POWER);
        }
        else
        {
            if(lift.getPower() == LIFT_DEFAULT_POWER)
            {
                if(lift.getCurrentPosition() < LIFT_HOLD_POSITION)
                {
                    lift.setPower(LIFT_HOLD_HIGH);
                }
                else
                {
                    lift.setPower(LIFT_HOLD_LOW);
                }
            }
        }

        if(linearOpMode.gamepad2.y)
        {
            blockArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            blockArm.setTargetPosition(0);

            blockArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }



    public void moveArm(int target, double power, int tolerance, String message, Telemetry telemetry)
    {
        int delta = blockArm.getTargetPosition() - target;

        blockArm.setPower(power);

        blockArm.setTargetPosition(target);

        if(delta < 0)
        {
            while(blockArm.getCurrentPosition() < blockArm.getTargetPosition() - tolerance && linearOpMode.opModeIsActive())
            {
                telemetry.addData("Arm Movement", message);
                telemetry.addData("Current Position", blockArm.getCurrentPosition());
                telemetry.addData("Target Position", blockArm.getTargetPosition());
                telemetry.addData("Tolerance", tolerance);
                telemetry.update();
            }
        }
        else if(delta > 0)
        {
            while(blockArm.getCurrentPosition() > blockArm.getTargetPosition() + tolerance && linearOpMode.opModeIsActive())
            {
                telemetry.addData("Arm Movement", message);
                telemetry.addData("Current Position", blockArm.getCurrentPosition());
                telemetry.addData("Target Position", blockArm.getTargetPosition());
                telemetry.addData("Tolerance", tolerance);
                telemetry.update();
            }
        }
    }


    public void foundationUp()
    {
        foundation_mover.setPosition(FOUNDATION_MOVER_OPEN);
    }

    public  void foundationDown()
    {
        foundation_mover.setPosition(FOUNDATION_MOVER_CLOSE);
    }

    public void openGrabber()
    {
        grabber_servo.setPosition(GRABBER_OPEN);
    }

    public void closeGrabber()
    {
        grabber_servo.setPosition(GRABBER_CLOSE);
    }

    public void initGrabber()
    {
        grabber_servo.setPosition(GRABBER_INIT);
    }

    public void intake_In()
    {
        intake_left.setPosition(.9);
        intake_right.setPosition(.04);
    }

    public void intake_Out()
    {
        intake_left.setPosition(.04);
        intake_right.setPosition(.9);
    }

    public void intake_Stop()
    {
        intake_left.setPosition(.5);
        intake_right.setPosition(.5);
    }

    public void capstone_on()
    {
        closeGrabber();

        linearOpMode.sleep(200);

        capstone.setPosition(CAPSTONE_CLOSE);
    }

    public  void capstone_off()
    {
        capstone.setPosition(CAPSTONE_OPEN);
    }

    public void push_stone(){stone_pusher.setPosition(STONE_PUSH);}
    public void stone_push_home(){stone_pusher.setPosition(STONE_PUSH_HOME);}

    public void auto_arm_home(){auto_arm.setPosition(AUTO_ARM_HOME);}
    public void auto_arm_down(){auto_arm.setPosition(AUTO_ARM_DOWN);}
}
