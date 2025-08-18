package ibm.newgen.mobility.remoteOperation.service;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RemoteRulesLoader {
	
	private final List<JsonObject> rules = new ArrayList<>();

	private final ResourcePatternResolver resourcePatternResolver;

	public RemoteRulesLoader(ResourcePatternResolver resourcePatternResolver) {
		this.resourcePatternResolver = resourcePatternResolver;
	}

	@PostConstruct
	public void loadRules() {
		try {
			if (CollectionUtils.isEmpty(rules)) {
				Gson gson = new Gson();
				Resource[] resources = resourcePatternResolver.getResources("classpath:*.json");
				for (Resource resource : resources) {
					JsonObject testJson = gson.fromJson(
							new InputStreamReader(getClass().getResourceAsStream("/" + resource.getFilename()),
									StandardCharsets.UTF_8),
							JsonObject.class);
					for (Map.Entry<String, JsonElement> entry : testJson.entrySet()) {
						JsonElement element = entry.getValue();
						if (element.isJsonArray()) {
							JsonArray jsonArray = element.getAsJsonArray();
							for (JsonElement arrayElement : jsonArray) {
								rules.add(arrayElement.getAsJsonObject());
							}
						}
					}
				}
			}
		} catch (Exception exception) {
			log.error("Failed to load the trip rules: " + exception);
		}
	}

	public List<JsonObject> getTripRules() {
		return this.rules;
	}
}
