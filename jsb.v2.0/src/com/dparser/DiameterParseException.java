package com.dparser;

import java.util.ArrayList;
import java.util.Iterator;

import com.dparser.DiameterHeader;
import com.dparser.DiameterMessage;

/*
 * To handle problems on Diameter message parse
 */
public class DiameterParseException extends DiameterException
{
  // Super class requires this
  private static final long serialVersionUID = 0;

  //private long errorReason = 0;

  // Support for multiple Failed-AVP
  private ArrayList<byte[]> failedAvpDataList = new ArrayList<byte[]>();

  // the reference for the message on process
  private DiameterMessage receivedMessage = null;

  // reference for Diameter header in the case of no reference to received
  // message or when the application wants to access only header information.
  // Therefore it refers to the header of receivedMessage when it is set.
  private DiameterHeader receivedHeader = null;

//  private ErroredAnswerExtension extensionHandler = null;

   /*
    * =========================================================================
    * Constuctors for any situation
    * =========================================================================
    */
  public DiameterParseException(long error, String message)
  {
    super(error, message, DiameterException.DIAMETER_PARSE_EXCEPTION);
  }

  public DiameterParseException(long error, DiameterHeader rcvHeader, String message)
  {
    super(error, message, DiameterException.DIAMETER_PARSE_EXCEPTION);
    this.receivedHeader  = rcvHeader;
  }

  public DiameterParseException(long error, DiameterMessage rcvMessage, String message)
  {
    super(error, message, DiameterException.DIAMETER_PARSE_EXCEPTION);
    this.receivedMessage = rcvMessage;
    this.receivedHeader  = rcvMessage.getHeader();
  }

  public DiameterParseException(long error, byte[] failedAvpData,
                         DiameterMessage rcvMessage, String message)
  {
    super(error, message, DiameterException.DIAMETER_PARSE_EXCEPTION);
    this.receivedMessage = rcvMessage;
    this.receivedHeader  = rcvMessage.getHeader();
    this.failedAvpDataList.add(failedAvpData);
  }

  public DiameterParseException(long error, ArrayList<byte[]> failedAvpList,
                         DiameterMessage rcvMessage, String message)
  {
    super(error, message, DiameterException.DIAMETER_PARSE_EXCEPTION);
    this.failedAvpDataList = failedAvpList;
    this.receivedMessage   = rcvMessage;
    this.receivedHeader    = rcvMessage.getHeader();
  }

  public DiameterParseException(DiameterMessage rcvMessage, DiameterException exp) {
    super(exp.getErrorReason(), exp.getMessage(), exp, DiameterException.DIAMETER_PARSE_EXCEPTION);
    this.receivedMessage = rcvMessage;
    this.receivedHeader  = rcvMessage.getHeader();
  }

   /*
    * =========================================================================
    * Accessors and Mutators
    * =========================================================================
    */

  public DiameterMessage getReceivedMessage()
  {
    return receivedMessage;
  }

  public DiameterHeader getReceivedHeader()
  {
    if (receivedMessage != null)
    {
      return receivedMessage.getHeader();
    }
    else
    {
      return receivedHeader;
    }
  }

  public ArrayList<byte[]> getFailedAvpList()
  {
    return this.failedAvpDataList; 
  }
  
  public void addFailedAvp(byte[] failedData)
  {
    this.failedAvpDataList.add(failedData);
  }

//  public ErroredAnswerExtension getErroredAnswerExtension()
//  {
//    return this.extensionHandler;
//  }
//
//  public void setErroredAnswerExtension(ErroredAnswerExtension handler)
//  {
//    this.extensionHandler = handler;
//  }

  
   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(super.toString());
      printData(sb);
      return sb.toString();
   }
   /*
    * =========================================================================
    * Utility
    * =========================================================================
    */
  public void printData(StringBuilder buffer)
  {
    buffer.append("\nError Reason  = " + errorReason);
    if (receivedHeader != null)
    {
      buffer.append("\nCommand Code  = " + receivedHeader.getCommandCode());
      buffer.append("\nFlags Byte    = " + Integer.toBinaryString(receivedHeader.getFlagsByte() & 0xFF));
      Iterator<byte[]> iterator = this.failedAvpDataList.iterator();
      while (iterator.hasNext())
      {
        byte[] data = iterator.next();
        int length =  data.length;
        buffer.append("\nfailedAvpData = " +
        //              DiameterUtilities.toHexString(iterator.next(), 0, iterator.next().length));
                      DiameterUtilities.toHexString(data, 0, length));
      }
    }
  }

}
