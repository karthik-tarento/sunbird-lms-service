package org.sunbird.common.quartz.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.quartz.JobExecutionContext;
import org.sunbird.actor.background.BackgroundOperations;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerUtil;
import org.sunbird.common.request.Request;
import org.sunbird.learner.util.Util;

/** @author Amit Kumar */
public class UpdateUserCountScheduler extends BaseJob {
  private static LoggerUtil logger = new LoggerUtil(UpdateUserCountScheduler.class);

  @Override
  public void execute(JobExecutionContext ctx) {
    logger.info(
        "UpdateUserCountScheduler:execute: Triggered Update user count Scheduler Job at: "
            + Calendar.getInstance().getTime()
            + " triggered by: "
            + ctx.getJobDetail().toString());
    List<Object> locIdList = new ArrayList<>();
    Util.DbInfo geoLocationDbInfo = Util.dbInfoMap.get(JsonKey.GEO_LOCATION_DB);
    Response response =
        cassandraOperation.getAllRecords(
            geoLocationDbInfo.getKeySpace(), geoLocationDbInfo.getTableName(), null);
    List<Map<String, Object>> list = (List<Map<String, Object>>) response.get(JsonKey.RESPONSE);
    for (Map<String, Object> map : list) {
      if (null == map.get(JsonKey.USER_COUNT) || 0 == ((int) map.get(JsonKey.USER_COUNT))) {
        locIdList.add(map.get(JsonKey.ID));
      }
    }
    logger.info(
        "UpdateUserCountScheduler:execute: size of total locId to processed = " + locIdList.size());
    Request request = new Request();
    request.setOperation(BackgroundOperations.updateUserCountToLocationID.name());
    request.getRequest().put(JsonKey.LOCATION_IDS, locIdList);
    request.getRequest().put(JsonKey.OPERATION, "UpdateUserCountScheduler");
    logger.info("UpdateUserCountScheduler:execute: calling BackgroundService actor from scheduler");
    tellToBGRouter(request);
  }
}
