package org.sunbird.user.service;

import java.util.List;
import java.util.Map;
import org.sunbird.common.request.RequestContext;

public interface UserExternalIdentityService {

  List<Map<String, Object>> getSelfDeclaredDetails(
      String userId, String orgId, String role, RequestContext context);

  List<Map<String, String>> getSelfDeclaredDetails(String userId, RequestContext context);

  List<Map<String, String>> getUserExternalIds(String userId, RequestContext context);

  String getUserV1(String extId, String provider, String idType, RequestContext context);

  String getUserV2(String extId, String orgId, String idType, RequestContext context);
}
