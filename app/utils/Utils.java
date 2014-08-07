package utils;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import models.basic.Config;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.w3c.dom.Document;

import play.Logger;
import play.Play;
import play.libs.Json;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


public class Utils {
    public static AtomicBoolean run;
    public static String serverIp;

    /**
     * Data para el generador de cadenas alfanumericas
     */
    private static char[] symbols;
    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        for (char ch = 'a'; ch <= 'z'; ++ch)
            tmp.append(ch);
        symbols = tmp.toString().toCharArray();
    }

    /**
	 * Metodo para imprimir errores al log
	 * @param invoker			job para obtener la ruta del error, puede ser null
	 * @param title				titulo para el mail, en caso de que sendMail sea true. si sendMail es false, este campo puede ser null
	 * @param description		descripcion del error a registrar
	 * @param sendMail			flag para definir si se debe enviar una notificacion por mail
	 * @param ex				excepcion generada por el error
	 * @param supportLevel		prioridad del soporte para este error
	 * @param loggerErrorType	flag para marcar el tipo de error de log a usar
	 */
	public static void printToLog(Object invoker, String title, String description, boolean sendMail, Throwable ex, String supportLevel, int loggerErrorType){
		StringBuilder message = new StringBuilder();
		if(invoker!=null){
			message.append(invoker);
		}
		message.append("{");
		if(!sendMail && title!=null && !title.isEmpty()){
			message.append(title);
			message.append(". ");
		}
		message.append(description);
		message.append("}");
		switch(loggerErrorType){
			case Config.LOGGER_ERROR: 
				if(ex == null){
					Logger.error(message.toString());
				}else{
					Logger.error(message.toString(),ex);
				}
				break;
			case Config.LOGGER_INFO: 
				if(ex == null){
					Logger.info(message.toString());
				}else{
					Logger.info(message.toString(),ex);
				}
				break;
			case Config.LOGGER_WARN:
				if(ex == null){
					Logger.warn(message.toString());
				}else{
					Logger.warn(message.toString(),ex);
				}
				break;
			case Config.LOGGER_DEBUG: 
				if(ex == null){
					Logger.debug(message.toString());
				}else{
					Logger.debug(message.toString(),ex);
				}
				break;
			case Config.LOGGER_TRACE: 
				if(ex == null){
					Logger.trace(message.toString());
				}else{
					Logger.trace(message.toString(),ex);
				}
				break;
		}

		if(sendMail){
            try{
                if(ex==null){
                    Alarm.sendMail(Config.getStringArray(supportLevel, ";"), title, message.toString());
                }else{
                    Alarm.sendMail(Config.getStringArray(supportLevel, ";"), title, message.toString(),ex);
                }
            } catch (Exception e) {
                Logger.error("Error mandando la alarma " + message.toString(),e);
            }
		}
	}
	
	/***
	 * Funcion que parsea un String json y lo devuelve en un Object(puede ser un ArrayList o un Map), usando LinkedHashMap para los items y ArrayList para los arreglos
	 * @param json	string a parsear
	 * @return		el mapa que contiene todos los values y keys que venian en el json
	 * @throws		Exception//org.json.simple.parser.ParseException
	 */
	public static ObjectNode parseJsonString(String json) throws Exception {
		try{
			ObjectNode jsonMap = (ObjectNode) Json.parse(json);
			return jsonMap;
		}catch(Exception ex){
			Alarm.sendMail(Config.getStringArray("support-level-1", ";"), "Error en parseJsonString", "No se pudo parsear: "+json, ex);
			throw ex;
		}
	}
	
	/***
	 * Funcion que que parsea un String json y lo devuelve en un Object(puede ser un ArrayList o un Map), usando LinkedHashMap para los items y ArrayList para los arreglos
	 * @param json	String con los campos del JSON que se generara
	 * @return		el mapa que contiene todos los values y keys que venian en el json
	 * @throws		Exception//org.json.simple.parser.ParseException
	 */
	@SuppressWarnings("unused")
	public static ObjectNode parseSafeJsonStringSMSC(String json) throws Exception {
		try{
			boolean correct = false;
			String newString = "";
			if(json.contains("HecticusSMSC")){
				for(int i=0; i<json.length(); i++){
					if(json.charAt(i)=='{' || json.charAt(i)=='['){
						newString = json.substring(i, json.length());
						try{
							Object obj = parseJsonString(newString);
							correct = true;
							break;
						}catch(Throwable ex){
							
						}
					}
				}
			}
			ObjectNode jsonMap = null;
			if(correct){
				jsonMap = (ObjectNode) Json.parse(newString);
			}else{
				jsonMap = (ObjectNode) Json.parse(json);
			}

			return jsonMap;
		}catch(Exception ex){
			Alarm.sendMail(Config.getStringArray("support-level-1", ";"), "Error en parseJsonString", "No se pudo parsear: "+json, ex);
			throw ex;
		}
	}
	
	/**
	 * Funcion que revisa si la respuesta es error (errorCode != 0)
	 * @param response	respuesta a revisar
	 * @return			true para error, false en caso contrario
	 */
	public static boolean checkIfResponseIsError(Object response){
		long errorCode = ((ObjectNode)response).get("error").asLong();
		return errorCode!=0;
	}
	
	public static Document getXMLfromString(String xml) throws Exception {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new ByteArrayInputStream(xml.getBytes()));
        return xmlDocument;
   }

    /***
     * Funcion que devuelve el timestamp actual en formato YYYYMMDDHHMMSS
     * @param tz	timezone a consultar
     * @return		fecha en formato YYYYMMDDHHMMSS
     */
    public static long currentTimeStamp(TimeZone tz) {

        Calendar actualDate = new GregorianCalendar(tz);
        int auxMonth = actualDate.get(Calendar.MONTH) + 1;
        int auxDay = actualDate.get(Calendar.DAY_OF_MONTH);
        int auxHour = actualDate.get(Calendar.HOUR_OF_DAY);
        int auxMinute = actualDate.get(Calendar.MINUTE);
        int auxSecond = actualDate.get(Calendar.SECOND);

        StringBuffer fechaInicio = new StringBuffer(12);
        fechaInicio.append(actualDate.get(Calendar.YEAR));
        if (auxMonth < 10) {
            fechaInicio.append("0");
        }
        fechaInicio.append(auxMonth);
        if (auxDay < 10) {
            fechaInicio.append("0");
        }
        fechaInicio.append(auxDay);
        if (auxHour < 10) {
            fechaInicio.append("0");
        }
        fechaInicio.append(auxHour);
        if (auxMinute < 10) {
            fechaInicio.append("0");
        }
        fechaInicio.append(auxMinute);
        if (auxSecond < 10) {
            fechaInicio.append("0");
        }
        fechaInicio.append(auxSecond);


        return Long.parseLong(fechaInicio.toString());
    }


    /**
     * Funcion que genera una cadena pseudoaleatoria alfanumerica de tamaño length
     * @param length El tamaño de la cadena
     * @return la cadena!!!
     */
    public static String tokenGenerator(int length){
        Random random = new Random();
        char[] buf = new char[length];
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
}
