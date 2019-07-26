
@cli
Feature: CommandLineUtilities
  Scenario: Use zmprov commands
    Given Create '10' bulk accounts with mask 'cat' on domain 'host'
    #Given Create Single account with username 'Auto' and password 'test123' on domain 'host' with attributes 'default'
