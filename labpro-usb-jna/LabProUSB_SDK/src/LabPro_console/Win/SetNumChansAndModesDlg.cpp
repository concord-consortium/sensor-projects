/*********************************************************************************

Copyright (c) 2012, Vernier Software & Technology
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of Vernier Software & Technology nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL VERNIER SOFTWARE & TECHNOLOGY BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

**********************************************************************************/
// SetNumChansAndModesDlg.cpp : implementation file
//

#include "stdafx.h"
#include "LabPro_console.h"
#include "SetNumChansAndModesDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CSetNumChansAndModesDlg dialog


CSetNumChansAndModesDlg::CSetNumChansAndModesDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CSetNumChansAndModesDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CSetNumChansAndModesDlg)
	m_num_chans = 0;
	m_bBinaryMode = FALSE;
	m_bRealTimeMode = FALSE;
	//}}AFX_DATA_INIT
}


void CSetNumChansAndModesDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CSetNumChansAndModesDlg)
	DDX_Text(pDX, IDC_NUM_CHANNELS, m_num_chans);
	DDV_MinMaxUInt(pDX, m_num_chans, 0, 99);
	DDX_Check(pDX, IDC_BINARY_MODE, m_bBinaryMode);
	DDX_Check(pDX, IDC_REAL_TIME_MODE, m_bRealTimeMode);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CSetNumChansAndModesDlg, CDialog)
	//{{AFX_MSG_MAP(CSetNumChansAndModesDlg)
		// NOTE: the ClassWizard will add message map macros here
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CSetNumChansAndModesDlg message handlers
