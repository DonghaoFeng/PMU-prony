///////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) Comets' Grp. of Kedong Corp 2008. All Rights Reserved.
//
// FileName     : message_inv.h
//
// Function     : interfaces of message bus
//
// Author       : MFY,YF,WH
//
// Date         : 2009.02.10
//
// Modify By    :
//
// Mod Date     :
//
//////////////////////////////////////////////////////////////////////////////////

#ifndef _MESSAGE_INV_H
#define _MESSAGE_INV_H

#include "messageheader.h"
#include "bob_inv.h"

#define  PRO_TCP         1
#define  PRO_UDP         3

class message_invocation: public bob_invocation
{
	public:
		int	m_init;//Add by MFY and WH 20090122

	public:
		message_invocation();
		~message_invocation();

		int messageInit(char *context_name=NULL, char *app_name=NULL, char *proc_name=NULL);
		int messageExit(int proc_key=-1);

		int messageSend(Message  *messageP, int messageLength, Msg_destination *msg_dst_p=NULL);
		int messageReceive(Message *messageP, Msg_source  *msg_src_p=NULL, int sync=1);

		int messageSubscribe(short set_id, char *context_name=NULL);
		int messageUnSubscribe(short set_id, char *context_name=NULL);
	private:
		int send_tcp_event(Message *messageP, int len, Msg_destination *p);

};

#endif


