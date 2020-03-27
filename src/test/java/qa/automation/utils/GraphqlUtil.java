package qa.automation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vimalselvam.graphql.GraphqlTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class GraphqlUtil {
    public static String prepareGraphqlPayload(Map<String, String> variables, String queryFileLocation) {
        File file = new File(queryFileLocation);
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            objectNode.put(entry.getKey(), entry.getValue());
        }
        String graphqlPayload = null;
        try {
            graphqlPayload = GraphqlTemplate.parseGraphql(file, objectNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return graphqlPayload;
    }
}