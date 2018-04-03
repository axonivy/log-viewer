package com.axonivy.ivy.supplements.logviewer;

import com.axonivy.ivy.supplements.logviewer.parser.LogLevel;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconUtil {
	
	  private static Image DEBUG_IMAGE = new Image("/images/" + "op_screwdriver_16.png");
	  private static Image INFO_IMAGE = new Image("/images/" + "ab_information_16.png");
	  private static Image WARN_IMAGE = new Image("/images/" + "ab_warning_16.png");
	  private static Image ERROR_IMAGE = new Image("/images/" + "ab_error_16.png");
	  private static Image FATAL_IMAGE = new Image("/images/" + "ns_bomb_16.png");

	
	  public static ImageView getIcon(LogLevel level)
	  {

	    Image icon;
	    switch (level)
	    {
	      case FATAL:
	    	icon = FATAL_IMAGE;
	    	break;

	      case ERROR:
	        icon = ERROR_IMAGE;
	        break;

	      case WARN:
	        icon = WARN_IMAGE;
	        break;

	      case INFO:
	        icon = INFO_IMAGE;
	        break;

	      default:
	        icon = DEBUG_IMAGE;
	        break;
	    }

	    return new ImageView(icon);
	  }
}
