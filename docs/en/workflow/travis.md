![travis](../../pics/travis.png)

We use [Travis CI](https://travis-ci.org/) for automating builds, tests and deployment.

Deployment includes publishing a github release and up to date project documentation as a github page.

Deployment on a showcase environment is made after merging a pull request on the main repository (INSEE)

Following steps apply during the integration process:

 - Build artifact
 - Build frontend application
 - Package frontend assets into war file
 - Deploy artifact on a docker container
 - Run unit tests and integration tests
 - Build documentation
 
## Configuration

Configuration is defined in .travis.yml:

[include](../../../.travis.yml)

## Building documentation

Documentation is build continuously with travis  [gitbook](https://www.gitbook.com/new) and publish on [project github page](https://pages.github.com/).

Documentation build will occure only on travis job refering to the branch 'zenika-dev' (and not on pull requests).*

Basically, documentation should hence be merged only when a pull request has been merged on the main repository (refer to [our workflow overview](./index.html))

Those steps are described in ```scripts/gitbook.sh```

Publishing on github pages implies a push on branch (branche gh-pages). To make it possible we need to:
 - [Generate an access token with a granted account](https://github.com/settings/tokens/new)
 - Give this token the 'repo' autorization scope
 - Create a GITHUB_TOKEN environment variable on [Travis dashboard](https://travis-ci.org/) with our token as a value
 
 ## Reporting
 
Each build generate a test coverage report published on [coveralls.io](https://coveralls.io/)
 
This page can be reached from the coverage badge include in the project README:
 
 [![Coverage Status](https://coveralls.io/repos/github/InseeFr/Pogues-Back-Office/badge.svg?branch=zenika-dev)](https://coveralls.io/github/InseeFr/Pogues-Back-Office?branch=zenika-dev)
 
 Just as for documentation we need to generate a token to grant access to [coveralls.io](https://coveralls.io)
 to Travis 

 - Binding this token to the COVERALLS_TOKEN environment variable on Travis we can now generate a report using the followin maven goal:
 
 ```bash
mvn -DrepoToken=$COVERALLS_TOKEN coveralls:report
```
 
