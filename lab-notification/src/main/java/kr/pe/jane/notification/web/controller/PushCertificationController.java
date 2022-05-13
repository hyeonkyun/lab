package kr.pe.jane.notification.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import kr.pe.jane.notification.common.Page;
import kr.pe.jane.notification.common.exception.PushError;
import kr.pe.jane.notification.common.exception.PushException;
import kr.pe.jane.notification.domain.model.PushCertificationInfo;
import kr.pe.jane.notification.service.IPushCertificationService;
import kr.pe.jane.notification.web.dto.PushCertificationParam;
import kr.pe.jane.notification.web.dto.PushResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping( value = "/push" )
public class PushCertificationController {

	@Inject
	IPushCertificationService pushCertificationService; 
	
	@PostMapping( value = "/certification", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
	public PushResponse pushCertificationRegister( @RequestBody PushCertificationParam pushCertificationParam, HttpServletRequest request ) throws PushException {
		log.debug( pushCertificationParam.toString() );
		
		if ( !pushCertificationParam.isVaild() ) {
			throw new PushException(PushError.PARAMETER_MISSING);
		}
		
		pushCertificationService.addPushCertification( pushCertificationParam );
		
		return new PushResponse( PushError.OK, PushError.toMessage(PushError.OK) );
	}
	
	@GetMapping( value = "/certifications/{page}/{size}", produces = MediaType.APPLICATION_JSON_VALUE )
	public PushResponse pushCertificationSearchList( @PathVariable ("page") int _page, @PathVariable ("size") int _size, HttpServletRequest request ) throws PushException {
		Map<String, Object> responseBody = new HashMap<String, Object>();		
		
		List<PushCertificationInfo> pushCertificationInfoList = pushCertificationService.getPushCertificationList( new Page( _page, _size ) );
		
		pushCertificationInfoList.forEach(action -> {
			try {
				action.toCertificationMap();
			} catch (PushException | JsonProcessingException e) {
				throw new PushException(PushError.INTERNAL_ERROR, PushError.toMessage(PushError.INTERNAL_ERROR), e);
			}
		});
		
		responseBody.put("page", _page);
		responseBody.put("size", _size);
		responseBody.put("total", pushCertificationService.getPushCertificationTotalCnt() );
		
		if ( pushCertificationInfoList != null && pushCertificationInfoList.size() > 0 ) {
			responseBody.put("pushCertificationInfoList", pushCertificationInfoList);
		}
		
		return new PushResponse( PushError.OK, PushError.toMessage(PushError.OK), responseBody );
	}
	
	@GetMapping( value = "/certification/{appId}", produces = MediaType.APPLICATION_JSON_VALUE )
	public PushResponse pushCertificationSearch( @PathVariable ("appId") String _appId, HttpServletRequest request ) throws PushException, JsonMappingException, JsonProcessingException {
		Map<String, Object> responseBody = new HashMap<String, Object>();
		
		PushCertificationInfo pushCertificationInfo = pushCertificationService.getPushCertification( _appId );
		
		if( pushCertificationInfo != null ) {
			pushCertificationInfo.toCertificationMap();
			responseBody.put("pushCertificationInfo", pushCertificationInfo);
		}
		
		return new PushResponse( PushError.OK, PushError.toMessage(PushError.OK), responseBody );
	}
	
	@PutMapping( value = "/certification/{appId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
	public PushResponse pushCertificationModify( @PathVariable ("appId") String _appId, @RequestBody PushCertificationParam pushCertificationParam, HttpServletRequest request ) throws PushException {
		log.debug( pushCertificationParam.toString() );
		
		pushCertificationParam.setAppId( _appId );
		
		if ( !pushCertificationParam.isVaild() ) {
			throw new PushException(PushError.PARAMETER_MISSING);
		}
		
		pushCertificationService.modifyPushCertification(pushCertificationParam);
		
		return new PushResponse( PushError.OK, PushError.toMessage(PushError.OK) );
	}
	
	@DeleteMapping( value = "/certification/{appId}", produces = MediaType.APPLICATION_JSON_VALUE )
	public PushResponse pushCertificationRemove( @PathVariable ("appId") String _appId, HttpServletRequest request ) throws PushException {
		pushCertificationService.removePushCertification( _appId );
		
		return new PushResponse( PushError.OK, PushError.toMessage(PushError.OK) );
	}	
}
