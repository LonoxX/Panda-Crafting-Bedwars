package de.pandacrafting.mc.util;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;

public class UUIDFetcher {

    private static final double PROFILES_PER_REQUEST = 100.0D;
    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";

    private final JSONParser jsonParser;
    private final List<String> names;
    private final boolean rateLimiting;

    public UUIDFetcher(@NotNull List<String> names, @NotNull boolean rateLimiting) {
        jsonParser = new JSONParser();
        this.names = ImmutableList.copyOf(names);
        this.rateLimiting = rateLimiting;
    }

    public UUIDFetcher(@NotNull List<String> names) {
        this(names, true);
    }

    private HttpURLConnection createConnection() throws Exception {
        var url = new URL("https://api.mojang.com/profiles/minecraft");
        var connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    public UUID fromBytes(@NotNull byte[] array) {
        if(array.length != 16) {
            throw new IllegalArgumentException("Illegal byte array length: " + array.length);
        } else {
            ByteBuffer byteBuffer = ByteBuffer.wrap(array);
            long mostSignificant = byteBuffer.getLong();
            long leastSignificant = byteBuffer.getLong();
            return new UUID(mostSignificant, leastSignificant);
        }
    }

    private UUID getUUID(@NotNull String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
    }

    public UUID getUUIDOf(@NotNull String name) throws Exception {
        return (new UUIDFetcher(Collections.singletonList(name))).call().get(name);
    }

    public byte[] toBytes(@NotNull UUID uuid) {
        var byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    private void writeBody(HttpURLConnection connection, String body) throws Exception {
        var stream = connection.getOutputStream();
        stream.write(body.getBytes());
        stream.flush();
        stream.close();
    }

    private Map<String, UUID> call() throws Exception {
        Map<String, UUID> uuidMap = new HashMap<>();
        int requests = (int)Math.ceil((double) names.size() / 100.0D);

        for(int i = 0; i < requests; ++i) {
            HttpURLConnection connection = createConnection();
            String body = JSONArray.toJSONString(names.subList(i * 100, Math.min((i + 1) * 100, names.size())));
            writeBody(connection, body);
            var array = (JSONArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            for(Object profile: array) {
                JSONObject jsonProfile = (JSONObject) profile;
                String id = (String) jsonProfile.get("id");
                String name = (String) jsonProfile.get("name");
                UUID uuid = getUUID(id);
                uuidMap.put(name, uuid);
            }
            if(rateLimiting && i != requests - 1) {
                Thread.sleep(100L);
            }
        }
        return uuidMap;
    }

}
