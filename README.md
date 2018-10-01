This repository has the test harness code for GraphQL tests.
# Building zm-api-test-harness

1. Create .zcs-deps directory at your %UserProfile%.
2. Install ant, ivy, Java1.8+ and set respective environment variables.
3. Download ant-contrib-1.0b1 jar file from below URL put into %UserProfile%/.zcs-deps location.
    ```
    https://files.zimbra.com/repository/ant-contrib/ant-contrib-1.0b1.jar
    ```
4. Clone following git repos:
    ```
    git clone https://github.com/Zimbra/zm-zcs.git
    git clone https://github.com/Zimbra/zimbra-package-stub.git
    ```
5. Build zm-api-test-harness.
   ```
   Go to zm-api-test-harness and build using following command.
         ant jar
 
   It will create testHarness.jar file
   
7. You can run testcases using build.xml or by adding debug confirmation run.
    ```
    Build.xml:
        ant runcukes
