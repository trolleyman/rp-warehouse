# Contribution Guide
## Branching
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
Merge the changes from the specified branch into the current one. (So to merge your finished changes into the master first checkout into master then merge your branch)
```
git merge <branch-name>
```
Delete a branch. Note this is a "safe" command. If there are unmerged changes you will not be allowed to delete the branch.
```
git branch -d <branch-name>
```

## Documenting
Firstly try as much as possible to make your code self documenting. You should be able to tell what the code does through its methods and varaibles. `getFile()` `sendCommand()` `itemCount` are good examples, they tell you what the method will do or what the varaible represents. Also try to keep names rather brief, longer is not necessarily better.

Next, JavaDoc classes and methods. If you are using an IDE like Eclipse then typing `**` then a new line automatiacly creates JavaDoc, inserting required field, return tags. At the very minimum breifly explain what the class/method does.

I recomend JavaDocs over just comments because in most IDEs (Eclipse for sure) you can get the JavaDoc description when calling the method and when hovering over the method.

Obviously you should comment wherever necessary, where what is happening could be unclear.