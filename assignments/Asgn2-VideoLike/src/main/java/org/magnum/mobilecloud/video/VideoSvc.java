package org.magnum.mobilecloud.video;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
public class VideoSvc implements VideoRepository {
	
	public static final String TITLE_PARAMETER = "title";
	public static final String ID_PARAMETER = "id";
	public static final String DURATION_PARAMETER = "duration";
	public static final String TOKEN_PATH = "/oauth/token";
	// The path where we expect the VideoSvc to live
	public static final String VIDEO_SVC_PATH = "/video";
	// The path to search videos by title
	public static final String VIDEO_TITLE_SEARCH_PATH = VIDEO_SVC_PATH + "/search/findByName";
	// The path to search videos by title
	public static final String VIDEO_DURATION_SEARCH_PATH = VIDEO_SVC_PATH + "/search/findByDurationLessThan";
	
	private Map<Long, Video> videos = new ConcurrentHashMap<Long, Video>();
	
	private static final AtomicLong currentId = new AtomicLong(0L);
	
	@RequestMapping(value=TOKEN_PATH, method=RequestMethod.POST)
	public @ResponseBody void login(
			@RequestBody Video v)
	{
		
	}
	
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
		v.setUrl(getDataUrl(v.getId()));
		return v;
	}
	
	@RequestMapping(value=VIDEO_SVC_PATH + "/{id}/like", method=RequestMethod.POST)
	public @ResponseBody void likeVideo(
			@PathVariable(ID_PARAMETER) long id,
			HttpServletResponse response)
	{
		
	}
	
	@RequestMapping(value=VIDEO_SVC_PATH + "/{id}/unlike", method=RequestMethod.POST)
	public @ResponseBody void unlikeVideo(
			@PathVariable(ID_PARAMETER) long id,
			HttpServletResponse response)
	{
		
	}
	
	@RequestMapping(value=VIDEO_TITLE_SEARCH_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByTitle(
			@PathVariable(TITLE_PARAMETER) String title)
	{
		return findByName(title);
	}
	
	@RequestMapping(value=VIDEO_DURATION_SEARCH_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(
			@PathVariable(DURATION_PARAMETER) long duration)
	{
		return findByDurationLessThan(duration);
	}
	
	@RequestMapping(value=VIDEO_SVC_PATH + "/{id}/likedby", method=RequestMethod.GET)
	public @ResponseBody Collection<String> getUsersWhoLikedVideo(
			@PathVariable("id") long id)
	{
		return null;
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

	@Override
	public <S extends Video> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Video> Iterable<S> save(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Video findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<Video> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Video> findAll(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Video entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Iterable<? extends Video> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<Video> findByName(String title) {
		// TODO Auto-generated method stub
		return null;
	}
}
