package stepDefinitions;

import org.junit.Assert;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import exception.HarnessException;
import harness.HarnessContext;
import harness.Constants.RootFolders;
import harness.utils.StringUtils;

/**
 * @author swapnil.pingle
 *
 */
public class SearchFolderMutations extends BaseStepDefs {
	private String searchFolderCreateMutation;
	public static String searchFolderId;
	private String searchFolderIdJsonPath = "data.searchFolderCreate.id";
	private Scenario scenario;
	private StringUtils stringUtils;
	private HarnessContext context;
	private String folderName;

	public SearchFolderMutations() {
		searchFolderCreateMutation = "'query':'mutation createSearchFolderMutation($csfInput:NewSearchFolderSpecInput!){searchFolderCreate(searchFolder: $csfInput ){ name,id }}'";
		stringUtils = new StringUtils();
	}

	public SearchFolderMutations(HarnessContext context) {
		this();
		this.context = context;
	}

	@Before
	public void BeforeSearchFolderMutations(Scenario scenario) {
		this.scenario = scenario;
	}

	@Given("^Create search folder for query '(.+)', name '(.+)', parentFolderId '(.+)', searchType '(.+)' using '(.+)' method$")
	public void createSearchFolder(String query, String name, String parentFolderId, String searchType, String method)
			throws HarnessException {
		String folderId = "";
		switch (parentFolderId) {
		case "defaultInbox":
			folderId = RootFolders.Inbox;
			break;
		case "defaultCalendar":
			folderId = RootFolders.Calendar;
			break;
		case "defaultBriefCase":
			folderId = RootFolders.BriefCase;
			break;
		default:
			if (parentFolderId.toLowerCase().startsWith("custom")) {
				folderId = parentFolderId.split(":")[1];
			} else {
				throw new HarnessException("Invalid parent folderId received");
			}
			break;
		}
		folderName = name + Math.random();
		context.setRuntimeData(folderName);
		String variables = "'variables':{'csfInput':{'query': '" + query + "','name': '" + folderName
				+ "','parentFolderId': '" + folderId + "','searchTypes': '" + searchType + "'}}";
		String requestBody = "{" + searchFolderCreateMutation + " , " + variables + "}";
		logger.info(requestBody);
		scenario.write("SearchFolderCreateMutation being processed :" + requestBody);
		baseline.processRequest(context, requestBody, method);
		searchFolderId = baseline.getValue(context, searchFolderIdJsonPath);
		scenario.write("SearchFolder Id returned :" + searchFolderId);
		Assert.assertTrue("Verify that searchFolder was created and valid search folder id is returned",
				!stringUtils.isEmpty(searchFolderId));
	}
}
