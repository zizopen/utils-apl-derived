package org.omnaest.utils.spring.session;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.omnaest.utils.web.HttpSessionFacade;
import org.omnaest.utils.web.HttpSessionFacadeFactory;

/**
 * The {@link HttpSessionService} allows to create {@link HttpSessionFacade} instances, as well as to resolve the
 * {@link HttpSession} directly or to manipulate it a regular {@link Map}.<br>
 * <br>
 * This Spring service bean should be instantiated as session bean with a given reference to a
 * {@link HttpSessionAndServletRequestResolverService} bean. Using annotation configuration will work since such a service is
 * declared as autowired.<br>
 * <br>
 * Spring configuration:
 * 
 * <pre>
 * &lt;context:annotation-config /&gt;
 * &lt;bean class=&quot;org.omnaest.utils.spring.session.implementation.HttpSessionAndServletRequestResolverServiceImpl&quot; /&gt;
 * &lt;bean class=&quot;org.omnaest.utils.spring.session.implementation.HttpSessionServiceImpl&quot; /&gt;
 * </pre>
 * 
 * @author Omnaest
 */
public interface HttpSessionService
{
  
  /**
   * Sets an attribute value for the given attribute name within the {@link HttpSession}
   * 
   * @param attributeName
   * @param value
   */
  public void setHttpSessionAttribute( String attributeName, Object value );
  
  /**
   * Resolves an attribute value from the {@link HttpSession} for the given attribute name
   * 
   * @param attributeName
   * @return
   */
  public Object getHttpSessionAttribute( String attributeName );
  
  /**
   * Returns a {@link Map} based view on the current {@link HttpSession}. Changes to the {@link Map} will be reflected by a change
   * of the {@link HttpSession} and vice versa.
   * 
   * @return
   */
  public Map<String, Object> resolveHttpSessionAndReturnAsMapView();
  
  /**
   * Resolves the {@link HttpSession}
   * 
   * @return
   */
  public HttpSession resolveHttpSession();
  
  /**
   * Creates a new {@link HttpSessionFacade} instance for the {@link HttpSession} of the current {@link Thread}
   * 
   * @see HttpSessionFacade
   * @see HttpSessionFacadeFactory
   * @param httpSessionFacadeType
   * @return
   */
  public <F extends HttpSessionFacade> F newHttpSessionFacade( Class<? extends F> httpSessionFacadeType );
  
}
