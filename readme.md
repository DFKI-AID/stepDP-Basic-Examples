
## Example Project
General Project Structure
other files. config file. how to start.
Tip: The step-dp is installed with its source files into your maven repo. Hence, you should be able to jump to the source for further insides within your IDE.

TOOD mark the important ones.


How to run the examples:
1. From IDE:
- You have to run [MyApp](/src/main/java/de/dfk/step/app/MyApp.java)
- There are two possibilities how you can change the dialog that is started:
	- Change the main dialog class in the [configuration file](/src/main/resources/application.yml)
	- Or Specify the dialog you want to start by adding `-Ddialog.name=de.dfki.step.dialog.MyDialog10` to your run configuration


2. From terminal: 
```bash
mvn clean package
# exchane MyDialog10 with your dialog 
java -Ddialog.name=de.dfki.step.dialog.MyDialog10 -jar target/mydialog.jar
```



### Rule-Based Dialog with Intent from Web-Gui
This example prints *Hello* into the terminal if a *greetings* intent was received.
See [MyDialog10.java](src/main/java/de/dfki/step/dialog/MyDialog10.java). 

After starting, visit the [web gui](http://localhost:50000/) and open the *Input* tab. Insert
```json
{
  "intent": "greetings",
  "confidence": 0.5
}
```
and press sendIntent.


- Task 1: Filter for the confidence: e.g. ignore if confidence is < 0.4
- Task 2: Allow greetings at most x times per second to occur. For a solution, check the function createGreetingsRule in the [MetaFactory](src/main/java/de/dfki/step/dialog/MetaFactory.java).
- Task 3: Decrease the necessary confidence if it was too low to trigger a rule. See [MyDialog11.java](src/main/java/de/dfki/step/dialo/MyDialog11.java) for a solution.

### Rule Coordination
input has the same origin, play around with priority. see dialog


### New Components / Extend the Web API
Loading confidence values for intent filtering from a separate component.
The confidence can be changed through a HTTP POST request.
See:
- [MyDialog30](src/main/java/de/dfki/step/dialog/MyDialog30.java)
- [ConfidenceController](src/main/java/de/dfki/step/web/ConfidenceController.java).

Example requests for setting the minimal confidence to 0.7.
```javascript
$.ajax({
                'url': '/confidence/swipe_gesture',
                'method': 'POST',
                'dataType': 'json',
                'contentType': 'application/json',
                'data': JSON.stringify({confidence: 0.7}),
                'processData': false
            });
````

```bash
curl localhost:50000/confidence/swipe_gesture -XPOST -d '{"confidence":0.7}' -H "Content-Type: application/json"
```

If you want to add html / js / ..., files, just put them into resurces/static.


### State Charts: Use SCXML for higher dialog modelling
- See
	- [MyDialog40](src/main/java/de/dfki/step/dialog/MyDialog40.java)
	- [MyDialog40.scxml](src/main/resources/sc/MyDialog40.scxml) (Created with qt creator. New File> Modeling > State Chart)




### Fusion: Create Intents from inputs.
See [MyDialog50](src/main/java/de/dfki/step/dialo/MyDialog50.java). Combine focus + speech and focus + gesture to rotate 3D model.

Send as inputs from the web gui to see the output in the terminal.
```json
{
  "intent": "rotate",
  "confidence": 0.6,
  "direction":"up"
}
```
or
```json
{
  "gesture": "down",
  "confidence": 0.6
}
```



### Dialog Coordination and Output
This examples extend the fusion example. 


### Schemas


### Rules for Meta-Dialog


### Overwrite Components





## Build and Run
Execute build.sh. Have a look at the script to change the example.

## Git Subtree info
This repo uses git subtrees for dependency management.
Usefule commands:

Add step-dp as remote. Do once after cloning.
```bash
git remote add -f step-dp ssh://git@lns-90165.sb.dfki.de:10022/i40/tractat/step-dp/step-dp.git
```

Download changes from step-dp into this project.
```bash
git fetch step-dp dev
git subtree pull -P external/step-dp step-dp dev --squash
```

If you updated step-dp from this repo, you upload the changes to a feature branch for merging.
```bash
git subtree push -P external/step-dp step-dp feature1
```

Only executed once after the repo was created. After the first commit.
```bash
git subtree add --prefix external/step-dp step-dp dev --squash
```

