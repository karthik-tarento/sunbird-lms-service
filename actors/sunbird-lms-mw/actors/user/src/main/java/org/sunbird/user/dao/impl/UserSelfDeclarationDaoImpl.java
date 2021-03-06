package org.sunbird.user.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.sunbird.cassandra.CassandraOperation;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.RequestContext;
import org.sunbird.helper.ServiceFactory;
import org.sunbird.models.user.UserDeclareEntity;
import org.sunbird.user.dao.UserSelfDeclarationDao;

public class UserSelfDeclarationDaoImpl implements UserSelfDeclarationDao {
  private CassandraOperation cassandraOperation = ServiceFactory.getInstance();
  private ObjectMapper mapper = new ObjectMapper();

  private static UserSelfDeclarationDao userSelfDeclarationDao = null;

  public static UserSelfDeclarationDao getInstance() {
    if (userSelfDeclarationDao == null) {
      userSelfDeclarationDao = new UserSelfDeclarationDaoImpl();
    }
    return userSelfDeclarationDao;
  }

  public void insertSelfDeclaredFields(Map<String, Object> extIdMap, RequestContext context) {

    cassandraOperation.insertRecord(
        JsonKey.SUNBIRD, JsonKey.USER_DECLARATION_DB, extIdMap, context);
  }

  public List<Map<String, Object>> getUserSelfDeclaredFields(
      UserDeclareEntity userDeclareEntity, RequestContext context) {
    List<Map<String, Object>> dbResExternalIds = new ArrayList<>();
    Map<String, Object> properties = new HashMap<>();
    properties.put(JsonKey.USER_ID, userDeclareEntity.getUserId());
    properties.put(JsonKey.ORG_ID, userDeclareEntity.getOrgId());
    properties.put(JsonKey.PERSONA, userDeclareEntity.getPersona());
    Response response =
        cassandraOperation.getRecordsByProperties(
            JsonKey.SUNBIRD, JsonKey.USER_DECLARATION_DB, properties, context);
    if (null != response && null != response.getResult()) {
      dbResExternalIds = (List<Map<String, Object>>) response.getResult().get(JsonKey.RESPONSE);
    }
    return dbResExternalIds;
  }

  public UserDeclareEntity upsertUserSelfDeclaredFields(
      UserDeclareEntity userDeclareEntity, RequestContext context) {
    Map<String, Object> compositeKey = new HashMap<>();
    compositeKey.put(JsonKey.USER_ID, userDeclareEntity.getUserId());
    compositeKey.put(JsonKey.ORG_ID, userDeclareEntity.getOrgId());
    compositeKey.put(JsonKey.PERSONA, userDeclareEntity.getPersona());
    Map<String, Object> updateFieldsMap = new HashMap<>();
    if (MapUtils.isNotEmpty(userDeclareEntity.getUserInfo())) {
      updateFieldsMap.put(JsonKey.USER_INFO, userDeclareEntity.getUserInfo());
    }
    updateFieldsMap.put(JsonKey.STATUS, userDeclareEntity.getStatus());
    updateFieldsMap.put(JsonKey.ERROR_TYPE, userDeclareEntity.getErrorType());
    updateFieldsMap.put(JsonKey.UPDATED_BY, userDeclareEntity.getUpdatedBy());
    updateFieldsMap.put(
        JsonKey.UPDATED_ON, new Timestamp(Calendar.getInstance().getTime().getTime()));
    cassandraOperation.updateRecord(
        JsonKey.SUNBIRD, JsonKey.USER_DECLARATION_DB, updateFieldsMap, compositeKey, context);
    return userDeclareEntity;
  }

  public List<Map<String, Object>> getUserSelfDeclaredFields(
      String userId, RequestContext context) {
    List<Map<String, Object>> dbResExternalIds = new ArrayList<>();
    Map<String, Object> properties = new HashMap<>();
    properties.put(JsonKey.USER_ID, userId);
    Response response =
        cassandraOperation.getRecordById(
            JsonKey.SUNBIRD, JsonKey.USER_DECLARATION_DB, properties, context);
    if (null != response && null != response.getResult()) {
      dbResExternalIds = (List<Map<String, Object>>) response.getResult().get(JsonKey.RESPONSE);
    }
    return dbResExternalIds;
  }

  public void deleteUserSelfDeclaredDetails(
      String userId, String orgId, String persona, RequestContext context) {
    Map<String, String> properties = new HashMap<>();
    properties.put(JsonKey.USER_ID, userId);
    properties.put(JsonKey.ORG_ID, orgId);
    properties.put(JsonKey.PERSONA, persona);
    cassandraOperation.deleteRecord(
        JsonKey.SUNBIRD, JsonKey.USER_DECLARATION_DB, properties, context);
  }
}
