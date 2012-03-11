/*******************************************************************************
 * Copyright 2011 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.omnaest.utils.download;

import java.util.HashMap;
import java.util.Map;

public class HTTPStatusCodeHelper
{
  public static HTTPStatusCodeDescription getStatusCodeDescription( int httpStatusCode )
  {
    //
    HTTPStatusCodeDescription retval = null;
    
    //
    Map<Integer, HTTPStatusCodeDescription> statusCodeDescriptionMap = HTTPStatusCodeHelper.getStatusCodeDescriptionMap();
    retval = statusCodeDescriptionMap.get( httpStatusCode );
    
    //
    return retval;
  }
  
  public static Map<Integer, HTTPStatusCodeDescription> getStatusCodeDescriptionMap()
  {
    //
    Map<Integer, HTTPStatusCodeDescription> statusCodeDescriptionMap = new HashMap<Integer, HTTPStatusCodeDescription>();
    
    //
    HTTPStatusCodeDescription httpStatusCodeDescription;
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               100,
                                                               "Continue",
                                                               "Die laufende Anfrage an den Server wurde noch nicht zur�ckgewiesen. (Wird im Zusammenhang mit dem �Expect: 100-continue�-Header-Feld verwendet[1]) Der Client kann nun mit der (potentiell sehr gro�en) Anfrage fortfahren." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               101,
                                                               "Switching Protocols",
                                                               "Wird verwendet, wenn der Server eine Anfrage mit gesetztem �Upgrade�-Header-Feld empfangen hat und mit dem Wechsel zu einem anderen Protokoll einverstanden ist." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 102, "Processing",
                                                               "Wird verwendet, um ein Timeout zu vermeiden, w�hrend der Server eine zeitintensive Anfrage bearbeitet[2]." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 200, "OK",
                                                               "Die Anfrage wurde erfolgreich bearbeitet und das Ergebnis der Anfrage wird in der Antwort �bertragen." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               201,
                                                               "Created",
                                                               "Die Anfrage wurde erfolgreich bearbeitet. Die angeforderte Ressource wurde vor dem Senden der Antwort erstellt. Das �Location�-Header-Feld enth�lt eventuell die Adresse der erstellten Ressource." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               202,
                                                               "Accepted",
                                                               "Die Anfrage wurde akzeptiert, wird aber zu einem sp�teren Zeitpunkt ausgef�hrt. Das Gelingen der Anfrage kann nicht garantiert werden." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 203, "Non-Authoritative Information",
                                                               "Die Anfrage wurde bearbeitet, das Ergebnis ist aber nicht unbedingt vollst�ndig und aktuell." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 204, "No Content",
                                                               "Die Anfrage wurde erfolgreich durchgef�hrt, die Antwort enth�lt jedoch bewusst keine Daten." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 205, "Reset Content",
                                                               "Die Anfrage wurde erfolgreich durchgef�hrt; der Client soll das Dokument neu aufbauen und Formulareingaben zur�cksetzen." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               206,
                                                               "Partial Content",
                                                               "Der angeforderte Teil wurde erfolgreich �bertragen (wird im Zusammenhang mit einem �Content-Range�-Header-Feld oder dem Content-Type multipart/byteranges verwendet). Kann einen Client �ber Teil-Downloads informieren (wird zum Beispiel von Wget genutzt, um den Downloadfortschritt zu �berwachen oder einen Download in mehrere Streams aufzuteilen)." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 207, "Multi-Status",
                                                               "Die Antwort enth�lt ein XML-Dokument, das mehrere Statuscodes zu unabh�ngig voneinander durchgef�hrten Operationen enth�lt." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               300,
                                                               "Multiple Choice",
                                                               "Die angeforderte Ressource steht in verschiedenen Arten zur Verf�gung. Die Antwort enth�lt eine Liste der verf�gbaren Arten. Das �Location�-Header-Feld enth�lt eventuell die Adresse der vom Server bevorzugten Repr�sentation." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               301,
                                                               "Moved Permanently",
                                                               "Die angeforderte Ressource steht ab sofort unter der im �Location�-Header-Feld angegebenen Adresse bereit. Die alte Adresse ist nicht l�nger g�ltig." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               302,
                                                               "Found",
                                                               "Die angeforderte Ressource steht vor�bergehend unter der im �Location�-Header-Feld angegebenen Adresse bereit[3]. Die alte Adresse bleibt g�ltig. Wird in HTTP/1.1 je nach Anwendungsfall durch die Statuscodes 303 bzw. 307 ersetzt. 302-Weiterleitung ist aufgrund eines Suchmaschinen-Fehlers, dem URL-Hijacking, in Kritik geraten. Webmaster sollten von der Verwendung eines solchen Redirects absehen, wenn sie auf fremde Inhalte weiterleiten." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 303, "See Other",
                                                               "Die Antwort auf die durchgef�hrte Anfrage l�sst sich unter der im �Location�-Header-Feld angegebenen Adresse beziehen." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               304,
                                                               "Not Modified",
                                                               "Der Inhalt der angeforderten Ressource hat sich seit der letzten Abfrage des Clients nicht ver�ndert und wird deshalb nicht �bertragen." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 305, "Use Proxy",
                                                               "Die angeforderte Ressource ist nur �ber einen Proxy erreichbar. Das �Location�-Header-Feld enth�lt die Adresse des Proxy." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 306, "(reserviert)",
                                                               "Code 306 wird nicht mehr verwendet ist aber reserviert. Es wurde f�r �Switch Proxy� verwendet." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               307,
                                                               "Temporary Redirect",
                                                               "Die angeforderte Ressource steht vor�bergehend unter der im �Location�-Header-Feld angegebenen Adresse bereit. Die alte Adresse bleibt g�ltig." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 400, "Bad Request",
                                                               "Die Anfrage-Nachricht war fehlerhaft aufgebaut." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               401,
                                                               "Unauthorized",
                                                               "Die Anfrage kann nicht ohne g�ltige Authentifizierung durchgef�hrt werden. Wie die Authentifizierung durchgef�hrt werden soll, wird im �WWW-Authenticate�-Header-Feld der Antwort �bermittelt." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 402, "Payment Required", "(reserviert)" );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               403,
                                                               "Forbidden",
                                                               "Die Anfrage wurde mangels Berechtigung des Clients nicht durchgef�hrt. Diese Entscheidung wurde � anders als im Fall des Statuscodes 401 � unabh�ngig von Authentifizierungsinformationen getroffen, auch etwa wenn eine als HTTPS konfigurierte URI nur mit HTTP aufgerufen wurde." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               404,
                                                               "Not Found",
                                                               "Die angeforderte Ressource wurde nicht gefunden. Dieser Statuscode kann ebenfalls verwendet werden, um eine Anfrage ohne n�heren Grund abzuweisen. Links, welche auf solche Fehlerseiten verweisen, werden auch als Tote Links bezeichnet." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               405,
                                                               "Method Not Allowed",
                                                               "Die Anfrage darf nur mit anderen HTTP-Methoden (zum Beispiel GET statt POST) gestellt werden. G�ltige Methoden f�r die betreffende Ressource werden im �Allow�-Header-Feld der Antwort �bermittelt." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               406,
                                                               "Not Acceptable",
                                                               "Die angeforderte Ressource steht nicht in der gew�nschten Form zur Verf�gung. G�ltige �Content-Type�-Werte k�nnen in der Antwort �bermittelt werden." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               407,
                                                               "Proxy Authentication Required",
                                                               "Analog zum Statuscode 401 ist hier zun�chst eine Authentifizierung des Clients gegen�ber dem verwendeten Proxy erforderlich. Wie die Authentifizierung durchgef�hrt werden soll, wird im �Proxy-Authenticate�-Header-Feld der Antwort �bermittelt." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 408, "Request Time-out",
                                                               "Innerhalb der vom Server erlaubten Zeitspanne wurde keine vollst�ndige Anfrage des Clients empfangen." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               409,
                                                               "Conflict",
                                                               "Die Anfrage wurde unter falschen Annahmen gestellt. Im Falle einer PUT-Anfrage kann dies zum Beispiel auf eine zwischenzeitliche Ver�nderung der Ressource durch Dritte zur�ckgehen." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 410, "Gone",
                                                               "Die angeforderte Ressource wird nicht l�nger bereitgestellt und wurde dauerhaft entfernt." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 411, "Length Required",
                                                               "Die Anfrage kann ohne ein �Content-Length�-Header-Feld nicht bearbeitet werden." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 412, "Precondition Failed",
                                                               "Eine in der Anfrage �bertragene Voraussetzung, zum Beispiel in Form eines �If-Match�-Header-Felds, traf nicht zu." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               413,
                                                               "Request Entity Too Large",
                                                               "Die gestellte Anfrage war zu gro�, um vom Server bearbeitet werden zu k�nnen. Ein �Retry-After�-Header-Feld in der Antwort kann den Client darauf hinweisen, dass die Anfrage eventuell zu einem sp�teren Zeitpunkt bearbeitet werden k�nnte." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 414, "Request-URI Too Long",
                                                               "Die URI der Anfrage war zu lang. Ursache ist oft eine Endlosschleife aus Redirects." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 415, "Unsupported Media Type",
                                                               "Der Inhalt der Anfrage wurde mit ung�ltigem oder nicht erlaubtem Medientyp �bermittelt." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 416, "Requested range not satisfiable",
                                                               "Der angeforderte Teil einer Ressource war ung�ltig oder steht auf dem Server nicht zur Verf�gung." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               417,
                                                               "Expectation Failed",
                                                               "Verwendet im Zusammenhang mit einem �Expect�-Header-Feld. Das im �Expect�-Header-Feld geforderte Verhalten des Servers kann nicht erf�llt werden." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               418,
                                                               "I'm a Teapot",
                                                               "Dieser Code ist als Aprilscherz der IETF zu verstehen, welcher n�her unter RFC 2324, 'Hyper Text Coffee Pot Control Protocol', beschrieben ist." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 421, "There are too many connections from your internet address",
                                                               "Verwendet, wenn die Verbindungsh�chstzahl �berschritten wird" );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               422,
                                                               "Unprocessable Entity",
                                                               "Verwendet, wenn weder die R�ckgabe von Statuscode 415 noch 400 gerechtfertigt w�re, eine Verarbeitung der Anfrage jedoch zum Beispiel wegen semantischer Fehler abgelehnt wird." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 423, "Locked", "Die angeforderte Ressource ist zurzeit gesperrt." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 424, "Failed Dependency",
                                                               "Die Anfrage konnte nicht durchgef�hrt werden, weil sie das Gelingen einer vorherigen Anfrage voraussetzt." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               425,
                                                               "Unordered Collection",
                                                               "In den Entw�rfen von WebDav Advanced Collections definiert, aber nicht im �Web Distributed Authoring and Versioning (WebDAV) Ordered Collections Protocol�." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 426, "Upgrade Required",
                                                               "Der Client sollte auf Transport Layer Security (TLS/1.0) umschalten." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 500, "Internal Server Error",
                                                               "Dies ist ein �Sammel-Statuscode� f�r unerwartete Serverfehler." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               501,
                                                               "Not Implemented",
                                                               "Die Funktionalit�t, um die Anfrage zu bearbeiten, wird von diesem Server nicht bereitgestellt. Ursache ist zum Beispiel eine unbekannte oder nicht unterst�tzte HTTP-Methode." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               502,
                                                               "Bad Gateway",
                                                               "Der Server konnte seine Funktion als Gateway oder Proxy nicht erf�llen, weil er seinerseits eine ung�ltige Antwort erhalten hat." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               503,
                                                               "Service Unavailable",
                                                               "Der Server steht, zum Beispiel wegen �berlast oder Wartungsarbeiten, zurzeit nicht zur Verf�gung. Ein �Retry-After�-Header-Feld in der Antwort kann den Client auf einen Zeitpunkt hinweisen, zu dem die Anfrage eventuell bearbeitet werden k�nnte." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               504,
                                                               "Gateway Time-out",
                                                               "Der Server konnte seine Funktion als Gateway oder Proxy nicht erf�llen, weil er innerhalb einer festgelegten Zeitspanne keine Antwort von seinerseits benutzten Servern oder Diensten erhalten hat." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 505, "HTTP Version not supported",
                                                               "Die benutzte HTTP-Version (gemeint ist die Zahl vor dem Komma) wird vom Server nicht unterst�tzt oder abgelehnt." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 506, "Variant Also Negotiates", "" );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 507, "Insufficient Storage",
                                                               "Die Anfrage konnte nicht bearbeitet werden, weil der Speicherplatz des Servers dazu zurzeit nicht mehr ausreicht[4]." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription(
                                                               509,
                                                               "Bandwidth Limit Exceeded",
                                                               "Die Anfrage wurde verworfen, weil sonst die verf�gbare Bandbreite �berschritten werden w�rde (inoffizielle Erweiterung einiger Server)." );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    httpStatusCodeDescription = new HTTPStatusCodeDescription( 510, "Not Extended",
                                                               "Die Anfrage enth�lt nicht alle Informationen, die die angefragte Server-Extension zwingend erwartet" );
    statusCodeDescriptionMap.put( httpStatusCodeDescription.statusCode, httpStatusCodeDescription );
    
    //
    return statusCodeDescriptionMap;
  }
  
  public static class HTTPStatusCodeDescription
  {
    private int    statusCode  = -1;
    private String name        = null;
    private String description = null;
    
    public HTTPStatusCodeDescription( int statusCode, String name, String description )
    {
      this.statusCode = statusCode;
      this.name = name;
      this.description = description;
    }
    
    public int getStatusCode()
    {
      return this.statusCode;
    }
    
    public void setStatusCode( int statusCode )
    {
      this.statusCode = statusCode;
    }
    
    public String getName()
    {
      return this.name;
    }
    
    public void setName( String name )
    {
      this.name = name;
    }
    
    public String getDescription()
    {
      return this.description;
    }
    
    public void setDescription( String description )
    {
      this.description = description;
    }
  }
}
