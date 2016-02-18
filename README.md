# Branching
Try wherever to make a new branch for the feature/bug-fix you are developing. This allows the master branch to remain in a deployable state. Once you have finished developing the new code it can be merged back to the master branch.

#### Usage
Show the current branches
```
git branch *Only local branches*
git branch -a *Local and remote branches*
git branch -r *Only remote branches*
```
Create a new branch
```
git branch <branch-name>
git checkout <branch-name>
git checkout -b <branch-name> *The above two commands in one*
```
When there are upstream changes in another branch that you wish to pull into yours you can use git merge.
```
git checkout <branch-name>
git merge master
```
Once you are finished with your branch you can merge the changes into the master. 
```
git checkout master
git merge <branch-name>
```
If you are finished with the branch then you can delete it. Note this is a "safe" command. If there are unmerged changes you will not be allowed to delete the branch.
```
git branch -d <branch-name>
```

# Documenting
Firstly try as much as possible to make your code self documenting. You should be able to tell what the code does through its methods and varaibles. `getFile()` `sendCommand()` `itemCount` are good examples, they tell you what the method will do or what the varaible represents. Also try to keep names rather brief, longer is not necessarily better.

Next, JavaDoc classes and methods. If you are using an IDE like Eclipse then typing `**` then a new line automatiacly creates JavaDoc, inserting required field, return tags. At the very minimum breifly explain what the class/method does.

I recomend JavaDocs over just comments because in most IDEs (Eclipse for sure) you can get the JavaDoc description when calling the method and when hovering over the method.

Obviously you should comment wherever necessary, where what is happening could be unclear.

# Extra commands
I would also recommend running these commands at some point:
```
alias.plog log --decorate --oneline
alias.tree log --decorate --oneline --graph --all
```
This enables you to run `git tree` which gives you a good overview of the project. If I run git tree at the moment I get this:
```
*   cddc88e (HEAD, origin/gui, gui) Merge remote-tracking branch 'origin/master' into gui
|\
| * 4b97982 (origin/master, origin/HEAD, master) Changed Direction to be an angle clockwise from Y+
| * a55e2a3 Add Contribution Guidelines to README.md
| * 1e08c9c Add a Contribution Guide
* | ace6071 Added robot rendering
* | 5ad6876 Added rendering of grid
* |   5d0e0d8 Merge remote-tracking branch 'origin/master' into gui
|\ \
| |/
| * 9db2a2d Renamed root package to 'warehouse'
* | 80e879b Added basic GUI
|/
* 90bcbfa Added basic Robot and Direction classes
* 8c9fcae Initial commit
```
It also allows you to run `git plog`, which gives you a list of commits on the current branch from most recent to least recent.
```
4b97982 (HEAD, origin/master, origin/HEAD, master) Changed Direction to be an angle clockwise from Y+
a55e2a3 Add Contribution Guidelines to README.md
1e08c9c Add a Contribution Guide
9db2a2d Renamed root package to 'warehouse'
90bcbfa Added basic Robot and Direction classes
8c9fcae Initial commit
```