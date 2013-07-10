package actors;

import akka.actor.UntypedActor;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.WebSocket;

/**
 * The broker between the WebSocket and the StockActor(s).  The UserActor holds the connection and sends serialized
 * JSON data to the client.
 */

public class UserActor extends UntypedActor {

    private final String uuid;
    private final WebSocket.Out<JsonNode> out;
    
    public UserActor(String uuid, WebSocket.Out<JsonNode> out) {
        this.uuid = uuid;
        this.out = out;
    }
    
    public void onReceive(Object message) {

        if (message instanceof StockUpdate) {
            // push the stock to the client
            StockUpdate stockUpdate = (StockUpdate)message;
            ObjectNode stockUpdateMessage = Json.newObject();
            stockUpdateMessage.put("type", "stockupdate");
            stockUpdateMessage.put("symbol", stockUpdate.symbol());
            stockUpdateMessage.put("price", stockUpdate.price().doubleValue());
            out.write(stockUpdateMessage);
        }
        else if (message instanceof StockHistory) {
            // push the stock to the client
            StockHistory stockHistory = (StockHistory)message;

            ObjectNode stockUpdateMessage = Json.newObject();
            stockUpdateMessage.put("type", "stockhistory");
            stockUpdateMessage.put("symbol", stockHistory.symbol());

            ArrayNode historyJson = stockUpdateMessage.putArray("history");
            for (Object price : stockHistory.history()) {
                historyJson.add(((Number)price).doubleValue());
            }
            
            out.write(stockUpdateMessage);
        }

    }
}