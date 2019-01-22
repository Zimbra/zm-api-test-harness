
@cli
Feature: CommandLineUtilities
  Scenario: Use zmprov commands
    Given Create '10' bulk accounts with mask 'cat' on domain 'zdev-vm002.eng.zimbra.com'
    Given Create Single account with username 'Auto' and password 'test123' on domain 'zdev-vm002.eng.zimbra.com' with attributes 'default'
