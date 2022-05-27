package kr.pe.jane.notification.common;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;

import kr.pe.jane.notification.common.utils.ConvertUtils;
import kr.pe.jane.notification.domain.model.PushTransmitReq;
import kr.pe.jane.notification.web.dto.PushTransmitParam;

public class PushTransmitReqDataHolder {

	private static ThreadLocal<PushTransmitReq> REQUEST_HOLDER = new ThreadLocal<PushTransmitReq>();

	public static PushTransmitReq getRequestData() {
		return REQUEST_HOLDER.get();
	}

	public static PushTransmitReq setRequestData( String transmitReqId, PushTransmitParam pushTransmitParam, HttpServletRequest request) throws JsonProcessingException {
		PushTransmitReq pushTransmitReq = new PushTransmitReq();
		
		pushTransmitReq.setTransmitReqId( transmitReqId );
		pushTransmitReq.setAppId( pushTransmitParam.getAppId() );
		pushTransmitReq.setTransmitReqParam( ConvertUtils.objectToJsonString( pushTransmitParam ) );
		pushTransmitReq.setReqIp( request.getRemoteAddr() );
		pushTransmitReq.setReqDt( new Date() );

		REQUEST_HOLDER.set(pushTransmitReq);
		return pushTransmitReq;
	}
	
	public static void resetRequestData() {
		REQUEST_HOLDER.remove();
	}
}
