package org.omnaest.utils.spring.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.omnaest.utils.web.HttpServletRequestResolver;
import org.omnaest.utils.web.HttpSessionResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.RequestContextFilter;

/**
 * The {@link HttpSessionAndServletRequestResolverService} provides methods to resolve the {@link HttpSession} and the
 * {@link HttpServletRequest} for a thread. It also can be injected as {@link Service}. It is based on the
 * {@link RequestContextHolder} of Spring.<br>
 * <br>
 * This needs the spring {@link RequestContextListener} or {@link RequestContextFilter} to be enabled within the web.xml. <br>
 * <br>
 * Spring configuration:
 * 
 * <pre>
 * &lt;bean class=&quot;org.omnaest.utils.spring.session.implementation.HttpSessionAndServletRequestResolverServiceImpl&quot; /&gt;
 * </pre>
 * 
 * @see #resolveHttpSession()
 * @see HttpSessionResolver
 * @see HttpServletRequestResolver
 * @author Omnaest
 */
public interface HttpSessionAndServletRequestResolverService extends HttpSessionResolver, HttpServletRequestResolver
{
  /**
   * Resolves the {@link HttpServletRequest} using the spring {@link RequestContextHolder} from the {@link Thread}s request
   * context. Returns null if no {@link HttpServletRequest} exists.
   */
  @Override
  public HttpServletRequest resolveHttpServletRequest();
  
  /**
   * Resolves the {@link HttpSession} using the spring {@link RequestContextHolder} from the {@link Thread}s request context.
   * Returns null if no {@link HttpSession} exists.
   * 
   * @return
   */
  @Override
  public HttpSession resolveHttpSession();
  
}
