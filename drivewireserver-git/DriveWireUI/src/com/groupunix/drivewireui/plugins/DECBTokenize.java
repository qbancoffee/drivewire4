package com.groupunix.drivewireui.plugins;

public class DECBTokenize
{

	private static String[] coco_functions = new String[]{"SGN", "INT", "ABS", "USR", "RND", "SIN", "PEEK",
			"LEN", "STR$", "VAL", "ASC", "CHR$", "EOF", "JOYSTK",
			"LEFT$", "RIGHT$", "MID$", "POINT", "INKEY$", "MEM",
			"ATN", "COS", "TAN", "EXP", "FIX", "LOG", "POS", "SQR",
			"HEX$", "VARPTR", "INSTR", "TIMER", "PPOINT", "STRING$",
			"CVN", "FREE", "LOC", "LOF", "MKN$", "AS", "", "LPEEK",
			"BUTTON", "HPOINT", "ERNO", "ERLIN"};
	
	

	@SuppressWarnings("unused")
	private static String[] dragon_functions = new String[]{"SGN", "INT", "ABS", "POS", "RND", "SQR", "LOG",
			"EXP", "SIN", "COS", "TAN", "ATN", "PEEK", "LEN",
			"STR$", "VAL", "ASC", "CHR$", "EOF", "JOYSTK",
			"FIX", "HEX$", "LEFT$", "RIGHT$", "MID$", "POINT", "INKEY$", "MEM",
			"VARPTR", "INSTR", "TIMER", "PPOINT", "STRING$", "USR", "LOF",
			"FREE", "ERL", "ERR", "HIMEM", "LOC", "FRE$" };

	private static String[] coco_commands = new String[]{"FOR", "GO", "REM", "'", "ELSE", "IF",
			"DATA", "PRINT", "ON", "INPUT", "END", "NEXT",
			"DIM", "READ", "RUN", "RESTORE", "RETURN", "STOP",
			"POKE", "CONT", "LIST", "CLEAR", "NEW", "CLOAD",
			"CSAVE", "OPEN", "CLOSE", "LLIST", "SET", "RESET",
			"CLS", "MOTOR", "SOUND", "AUDIO", "EXEC", "SKIPF",
			"TAB(", "TO", "SUB", "THEN", "NOT", "STEP",
			"OFF", "+", "-", "*", "/", "^",
			"AND", "OR", ">", "=", "<", "DEL",
			"EDIT", "TRON", "TROFF", "DEF", "LET", "LINE", "PCLS",
			"PSET", "PRESET", "SCREEN", "PCLEAR", "COLOR", "CIRCLE",
			"PAINT", "GET", "PUT", "DRAW", "PCOPY", "PMODE",
			"PLAY", "DLOAD", "RENUM", "FN", "USING", "DIR",
			"DRIVE", "FIELD", "FILES", "KILL", "LOAD", "LSET",
			"MERGE", "RENAME", "RSET", "SAVE", "WRITE", "VERIFY",
			"UNLOAD", "DSKINI", "BACKUP", "COPY", "DSKI$", "DSKO$",
			"DOS", "WIDTH", "PALETTE", "HSCREEN", "LPOKE", "HCLS",
			"HCOLOR", "HPAINT", "HCIRCLE", "HLINE", "HGET", "HPUT",
			"HBUFF", "HPRINT", "ERR", "BRK", "LOCATE", "HSTAT",
			"HSET", "HRESET", "HDRAW", "CMP", "RGB", "ATTR"};

	@SuppressWarnings("unused")
	private static String[] dragon_commands= new String[]{"FOR", "GO", "REM", "'", "ELSE", "IF", "DATA", "PRINT",
			  "ON", "INPUT", "END", "NEXT", "DIM", "READ", "LET", "RUN",
			  "RESTORE", "RETURN", "STOP", "POKE", "CONT", "LIST", "CLEAR",
			  "NEW", "DEF", "CLOAD", "CSAVE", "OPEN", "CLOSE", "LLIST",
			  "SET", "RESET", "CLS", "MOTOR", "SOUND", "AUDIO", "EXEC",
			  "SKIPF", "DEL", "EDIT", "TRON", "TROFF", "LINE", "PCLS", "PSET",
			  "PRESET", "SCREEN", "PCLEAR", "COLOR", "CIRCLE", "PAINT",
			  "GET", "PUT", "DRAW", "PCOPY", "PMODE", "PLAY", "DLOAD", "RENUM",
			  "TAB(", "TO", "SUB", "FN", "THEN", "NOT", "STEP", "OFF", "+",
			  "-", "*", "/", "^", "AND", "OR", ">", "=", "<", "USING", "AUTO",
			  "BACKUP", "BEEP", "BOOT", "CHAIN", "COPY", "CREATE", "DIR",
			  "DRIVE", "DSKINIT", "FREAD", "FWRITE", "ERROR", "KILL", "LOAD",
			  "MERGE", "PROTECT", "WAIT", "RENAME", "SAVE", "SREAD", "SWRITE",
			  "VERIFY", "FROM", "FLREAD", "SWAP"};

	public static byte[] detokenize(byte[] in_buffer)
	{
		String res = "";
		int line_number = 0;
		int character;
		int in_pos = 0;
		
		if ((in_buffer[in_pos] & 0xFF) == 0xFF)
		{
			int filesize = (in_buffer[1] & 0xFF) << 8;
			filesize += (in_buffer[2] & 0xFF);
			in_pos = 3;
			
			if (in_buffer.length-3 != filesize)
			{
				return(("Header size bytes " + filesize + " != file size " + (in_buffer.length-3) + ", probably not BASIC").getBytes());
			}
			
		}
		
		@SuppressWarnings("unused")
		int startaddr;
		int nextaddr = startaddr = ((in_buffer[in_pos++] & 0xFF) << 8) + (in_buffer[in_pos++] & 0xFF);
		
		while((nextaddr != 0) && (in_pos < in_buffer.length))
		{
			line_number = (in_buffer[in_pos++] & 0xFF) << 8;
			line_number += (in_buffer[in_pos++] & 0xFF);
			
			res += line_number + " ";
			
			while (( (character = (in_buffer[in_pos++] &0xFF)) != 0 ) && (in_pos < in_buffer.length - 2))
			{
				
				if (( character == 0xff ) && ( in_pos < in_buffer.length - 3))
				{	
					character = (in_buffer[in_pos++] & 0xFF);
					
					if ((character > 0x7F) && (character - 0x80 < coco_functions.length))
					{
						res += coco_functions[character - 0x80];
					}
					else
					{
						res += "!";
					}
				}
				else if( character >= 0x80 )
				{
					/* A Command call */
					if (character - 0x80 < coco_commands.length )
					{
						res += coco_commands[character - 0x80];
					}
					else
					{
						res += "!";
					}
				}
				else if( character == ':' && ( (in_buffer[in_pos] & 0xFF) == 0x83 || (in_buffer[in_pos] & 0xFF) == 0x84) )
				{
					/* When colon-apostrophe is encountered, the colon is dropped. */
					/* When colon-ELSE is encountered, the colon is dropped. */
					
				}
				else
				{
					res += (char)character;
				}
			}
			
			nextaddr = (in_buffer[in_pos++] & 0xFF) << 8;
			nextaddr += (in_buffer[in_pos++] & 0xFF);

			res += "\r";
			
			// TODO - check pointer vs lengths?
			//if ((nextaddr > 0) && (in_pos + startaddr != nextaddr))
			//{
				//return("Mismatch next line pointer to line length (" + nextaddr + " vs " + (in_pos + startaddr) + "), corrupted file?");
			//}
		}
		
		
		return res.getBytes();
	}
	
	
}
