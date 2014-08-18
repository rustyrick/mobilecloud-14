package org.magnum.dataup;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoStatus.VideoState;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class VideoSvc {
	
	public static final String DATA_PARAMETER = "data";
	public static final String ID_PARAMETER = "id";
	public static final String VIDEO_SVC_PATH = "/video";
	public static final String VIDEO_DATA_PATH = VIDEO_SVC_PATH + "/{id}/data";
	
	private Map<Long, Video> videos = new ConcurrentHashMap<Long, Video>();
	
	private static final AtomicLong currentId = new AtomicLong(0L);
	
	@RequestMapping(value=VIDEO_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList()
	{
		return videos.values();
	}
	
	@RequestMapping(value=VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Video addVideo(
			@RequestBody Video v)
	{
		checkAndSetId(v);
		videos.put(v.getId(), v);
		v.setDataUrl(getDataUrl(v.getId()));
		return v;
	}
	
	@RequestMapping(value=VIDEO_DATA_PATH, method=RequestMethod.POST)
	public @ResponseBody VideoStatus setVideoData(
			@PathVariable(ID_PARAMETER) long id, 
			@RequestParam(DATA_PARAMETER) MultipartFile videoData,
			HttpServletResponse response)
	{
		try
		{
			Video v = videos.get(id);
			VideoFileManager vfm = VideoFileManager.get();
			if(v != null)
			{
				vfm.saveVideoData(v, videoData.getInputStream());
			}
			else
			{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		}
		catch(IOException aEx)
		{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		return new VideoStatus(VideoState.READY);
	}
	
	@RequestMapping(value=VIDEO_DATA_PATH, method=RequestMethod.GET)
	public @ResponseBody void getData(
			@PathVariable(ID_PARAMETER) long id,
			HttpServletResponse response)
	{
		try
		{
			Video v = videos.get(id);
			VideoFileManager vfm = VideoFileManager.get();
			if(v != null && vfm.hasVideoData(v))
			{
				vfm.copyVideoData(v, response.getOutputStream());
			}
			else
			{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		}
		catch(IOException aEx)
		{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	
	private void checkAndSetId(Video entity) {
        if(entity.getId() == 0){
            entity.setId(currentId.incrementAndGet());
        }
    }
	
	private String getDataUrl(long videoId){
        String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
        return url;
    }

    private String getUrlBaseForLocalServer() {
       HttpServletRequest request = 
           ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
       String base = 
          "http://"+request.getServerName() 
          + ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
       return base;
    }
}
