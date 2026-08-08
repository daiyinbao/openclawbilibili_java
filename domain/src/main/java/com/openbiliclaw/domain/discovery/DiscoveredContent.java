package com.openbiliclaw.domain.discovery;

import com.openbiliclaw.domain.source.SourcePlatform;
import java.util.List;
import java.util.Map;

public record DiscoveredContent(
        ContentId contentId,
        SourcePlatform sourcePlatform,
        String title,
        String authorName,
        String contentUrl,
        String description,
        List<String> tags,
        Map<String, String> metadata
) {
}

