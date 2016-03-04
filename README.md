# Setting Up Your Environment
There are three seperate projects inside of the repo, `pc` `nxt` and `shared`. 

First create a new LejosNXT project inside `shared` with `rp-utils` as a required project. Add LejosNXT as a libary. 

Then create a new LejosNXT project inside `nxt` with `shared` and `rp-utils` as required projects. Add LejosNXT as a libary.  

Finally create a new LejosPC project inside `pc` with `shared` and `rp-utils` as required projects. Add LejosPC as a libary.

# Merge Conflicts
Sometimes you may try to push your code and git shouts at you saying to pull first. So you pull and then git complains about merge conflicts. This happens because someone else has pushed commits to the current branch after your last pull. Your files will have filled with merge conflicts wherever git cannot automatically merge. It will look something like this:
```
<<<<<<< HEAD
public void doThis2(String someText) {
=======
public void doThis(int aNumber, String someText) {
>>>>>>> some-massive-commit-hash
```
The text in the first block is what you have in your local repository and the second is what is being pulled from the remote repository. All you have to do is choose which (or what combination) commit you want to keep. Say I want to keep the method name `doThis2` but the arguments of `doThis`. I would change one of the parts of code:
```
<<<<<<< HEAD
public void doThis2(int aNumber, String someText) { *This is what I'm keeping*
=======
public void doThis(int aNumber, String someText) {
>>>>>>> some-massive-commit-hash
```
Then delete the rest:
```
public void doThis2(int aNumber, String someText) {
```
Finally do this to all of the files with merge problems, `git add` the files and commit them as normal. Conflicts resolved!
# Branching
Try wherever to make a new branch for the feature/bug-fix you are developing. This allows the master branch to remain in a deployable state. Once you have finished developing the new code it can be merged back to the master branch.

## Usage
Show the current branches
```
git branch *Only local branches*
git branch -a *Local and remote branches*
git branch -r *Only remote branches*
```
Get a remote branch from the repo. Do this when someone has created a branch you want to pull into your local repo.
```
git fetch *gets all changes from the remote repo*
git checkout <branch-name> *set up a local branch to track the remote*
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
The first command will delete the branch in your local repo whilst the second deletes it from the origin (online repo).
```
git branch -d <branch-name>
git push origin --delete <branch-name> 
```

# Documenting
Firstly try as much as possible to make your code self documenting. You should be able to tell what the code does through its methods and varaibles. `getFile()` `sendCommand()` `itemCount` are good examples, they tell you what the method will do or what the varaible represents. Also try to keep names rather brief, longer is not necessarily better.

Next, JavaDoc classes and methods. If you are using an IDE like Eclipse then typing `**` then a new line automatiacly creates JavaDoc, inserting required field, return tags. At the very minimum breifly explain what the class/method does.

I recomend JavaDocs over just comments because in most IDEs (Eclipse for sure) you can get the JavaDoc description when calling the method and when hovering over the method.

Obviously you should comment wherever necessary, where what is happening could be unclear.

# Extra commands
I would also recommend running these commands at some point:
```
git config --global alias.plog log --decorate --oneline
git config --global alias.tree log --decorate --oneline --graph --all
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