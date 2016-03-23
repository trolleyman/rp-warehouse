#UNDERSTANDING THE COMMANDS
##Introduction
The route planners (both single robot and multi robot) return several different commands from the Command enum for the robot to follow. It is essential that the exact purpose of each of these commands is translated to instructions for the robot, because one mistake will cock up the whole route plan, and that's bad.

##The Commands
###Direction Commands
####FORWARDS
The robot travels straight on at the next junction and proceeds along the line
####LEFT
The robot turns left at the next junction and proceeds along the line
####RIGHT
The robot turns right at the next junction and proceeds along the line
####BACKWARDS
The robot turns around at the next junction and proceeds along the line

###Function Commands
####PICK
The robot should stop at the next junction and await items to be loaded onto it.
####DROP
The robot should stop at the next junction and 'unload' the items (although presumably this is automatic).

###Wait Commands
####WAIT
The robot should wait until all three robots have the command WAIT
This command is triggered after a successful pick or drop, although if a robot is waiting to use the same location there may be an additional command in between.
###WAIT_ESC
The robot should wait until the other two robots have the command WAIT.
This command is triggered when a robot is already in the space this robot wants to occupy, so it waits next to it until the space is available.
