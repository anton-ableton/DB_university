package tables;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonUtils {

  private static final String JSON_FILE_PATH = "src/main/resources/id_info.json";

  public static JsonObject readJsonFile() {
    try (FileReader reader = new FileReader(JSON_FILE_PATH)) {
      JsonParser parser = new JsonParser();
      return parser.parse(reader).getAsJsonObject();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void writeJsonFile(JsonObject jsonObject) {
    try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      gson.toJson(jsonObject, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static int getField(String fieldName) {
    JsonObject jsonObject = readJsonFile();
    if (jsonObject != null && jsonObject.has(fieldName)) {
      return jsonObject.get(fieldName).getAsInt();
    }
    return -1;
  }

  public static void incrementField(String fieldName) {
    JsonObject jsonObject = readJsonFile();
    if (jsonObject != null && jsonObject.has(fieldName)) {
      int fieldValue = jsonObject.get(fieldName).getAsInt();
      jsonObject.addProperty(fieldName, fieldValue + 1);
      writeJsonFile(jsonObject);
    }
  }

}
