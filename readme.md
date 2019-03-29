

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

Only executed once after the repo was created.
```bash
git subtree add --prefix external/step-dp step-dp dev --squash
```

