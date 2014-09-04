package org.magnum.mobilecloud.video;

import java.security.Principal;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

@Controller
public class VideoSvc {
	
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
		
	@Autowired
	private VideoRepository videoRepo;
		
	@RequestMapping(value=VIDEO_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList()
	{
		return Lists.newArrayList(videoRepo.findAll());
	}
	
	@RequestMapping(value=VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Video addVideo(
			@RequestBody Video v)
	{
		return videoRepo.save(v);
	}
	
	@RequestMapping(value=VIDEO_SVC_PATH + "/{id}", method=RequestMethod.GET)
	public @ResponseBody Video getVideoById(
			@PathVariable(ID_PARAMETER) long id,
			HttpServletResponse response)
	{
		Video v = videoRepo.findOne(id);
		if(v == null)
		{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		return v;
	}
	
	@RequestMapping(value=VIDEO_SVC_PATH + "/{id}/like", method=RequestMethod.POST)
	public @ResponseBody void likeVideo(
			@PathVariable(ID_PARAMETER) long id,
			Principal p,
			HttpServletResponse response)
	{
		Video v = videoRepo.findOne(id);
		if(v == null)
		{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		if(!v.getLikers().contains(p.getName()))
		{
			v.likeVideo(p.getName());
			v.incrementLikeCount();
			videoRepo.save(v);
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	@RequestMapping(value=VIDEO_SVC_PATH + "/{id}/unlike", method=RequestMethod.POST)
	public @ResponseBody void unlikeVideo(
			@PathVariable(ID_PARAMETER) long id,
			Principal p,
			HttpServletResponse response)
	{
		Video v = videoRepo.findOne(id);
		if(v == null)
		{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		if(v.getLikers().contains(p.getName()))
		{
			v.unlikeVideo(p.getName());
			v.decrementLikeCount();
			videoRepo.save(v);
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
	@RequestMapping(value=VIDEO_TITLE_SEARCH_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByTitle(
			@RequestParam(TITLE_PARAMETER) String title)
	{
		return videoRepo.findByName(title);
	}
	
	@RequestMapping(value=VIDEO_DURATION_SEARCH_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(
			@RequestParam(DURATION_PARAMETER) long duration)
	{
		return videoRepo.findByDurationLessThan(duration);
	}
	
	@RequestMapping(value=VIDEO_SVC_PATH + "/{id}/likedby", method=RequestMethod.GET)
	public @ResponseBody Collection<String> getUsersWhoLikedVideo(
			@PathVariable("id") long id)
	{
		return videoRepo.findOne(id).getLikers();
	}
}
