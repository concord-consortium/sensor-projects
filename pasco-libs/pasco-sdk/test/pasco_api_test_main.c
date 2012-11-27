/**

win-console-main.c : Console test application.

Test all but bulk get API. (Not yet implemented.)
*/

#include "pasco_api.h"
#include <stdio.h>
#include <stdlib.h> 
#include <memory.h>


static void DumpBuffer (const void *pvBuf, int lLen);
static void print_int_arr(int* arr, int len);
static int pasco_rate_ms(int rate);
static void print_pasco_rate(int rate);



static void print_banner();

static void print_devices(int* devarr, int ndev)
{

	if(ndev == 0)
	{
		printf("No devices are attached\n");
		return ;
	}

	if(ndev < 0)
	{
		printf("Error finding devices %d\n", ndev);
		return;
	}


	{
		int j =0;
		if(ndev == 1)
			printf("One device is attached\n");
		else
			printf("%d devices are attached\n",ndev);

		for(j=0;j<ndev;j++)
		{
			printf("Device # %d \n", devarr[j]);
		}
	}

	

}









#define DEVARRSIZ 20
#define STRSIZ 64
#define READSIZ 256



static void test_find_devices(int handle)
{
	int devarr[DEVARRSIZ];
	int ndev = -1;
	int i = 0;

	for(i=0; i <3; i++)
	{
		printf("\n**\n Plug/Unplug USB cables, \n Press enter \n**\n");

		getchar();

		ndev = PasGetDevices(handle, devarr, DEVARRSIZ);
		print_devices(devarr,ndev);

	}

}


static void test_print_some_good_stuff_for_all_sensors(int handle)
{

	int retcode = -1;
	char strbuf[STRSIZ];

	int devarr[DEVARRSIZ];
	int device = -1;
	int samplesize = -1;
	int ratemin = -1, ratedef = -1, ratemax = -1;
	int nchan = -1;
	int ndev = -1;
	int i = 0, j=0, k=0;
	int nmeasurements = -1;
	int probe_validity_return = -1;
	char probe_validity_bits[4];
	
	ndev = PasGetDevices(handle, devarr, DEVARRSIZ);

	if(ndev<0)
	{
		printf("test_print_some_good_stuff_for_all_sensors Error %d\n",ndev);
		return;
	}

	for(i=0; i < ndev ; i++)
	{
		device = devarr[i];

		printf("Device # %d, ProductID %d\n", device, PasGetDeviceProductID(handle, device));
		
		nchan = PasGetNumChannels(handle,device);

		for(j=0;j<nchan;j++)
		{
			if(1!=PasGetExistChannel(handle,device,j))
				continue;

			samplesize  = PasGetSampleSize(handle, device, j);
			printf("Device # %d, Channel %d, sample size: %d\n",device, j, samplesize);
			ratemin = PasGetSampleRateMinimum(handle, device, j);
			ratedef = PasGetSampleRateDefault(handle, device, j);
			ratemax = PasGetSampleRateMaximum(handle, device, j);
			printf("       Sample rates: min %d, default %d, max %d\n",ratemin,ratedef, ratemax);



			strbuf[0] = 0x00;
			retcode = PasGetName(handle, device, j,strbuf,STRSIZ);
			if(retcode>-1)
			printf("Device # %d, Channel %d, name: \"%s\"\n",device, j, strbuf);
			else
			printf("Device # %d, Channel %d, error: %d ( if -1 == just no Sensor)\n",device,j,retcode);
			
			nmeasurements = PasGetNumMeasurements(handle, device, j);
			for(k=0;k<nmeasurements;k++)
			{
				strbuf[0] = 0x00;
				retcode = PasGetMeasurementName(handle, device, j, k, strbuf, STRSIZ);
				if(retcode>-1) {
					printf("Device # %d, Channel %d, Measurement %d, name \"%s\"\n", device, j, k, strbuf);
				} else {
					printf("Device # %d, Channel %d, Measurement %d, error: %d\n", device, j, k, retcode);
				}
			}
			
			probe_validity_return = PasGetSupportsProbeValidityDetection(handle, device, j);
			printf("Device # %d, Channel %d, SupportsProbeValidity return %d\n", device, j, probe_validity_return);
			if(probe_validity_return>0){
				memset(probe_validity_bits, 0, 4);
				
				retcode = PasCheckMeasurementValidity(handle, device, j, probe_validity_return, probe_validity_bits);
				if (retcode > -1) {
					printf("Device # %d, Channel %d, CheckMeasurementValidity bits ", device, j);
					DumpBuffer(probe_validity_bits, probe_validity_return);
				} else {
					printf("Device # %d, Channel %d, CheckMeasurementValidity error %d\n", device, j, retcode);
				}
			}
			
			printf("Device # %d, Channel %d, SensorDetected %d\n", device, j, PasGetSensorDetected(handle, device, j));
			
			// Read data sheet
			{
				int dataSheetSize = -1;
				char *dataSheetBuffer = NULL;
				int dataSheetReadSize = -1;
				
				dataSheetSize = PasGetSensorDataSheetSize(handle,device, j);
				if(dataSheetSize < 0){
					printf("Device # %d, Channel %d, couldn't read datasheet size, error: %d\n",device,j,retcode);
					return;
				}
				dataSheetBuffer =(char *) malloc(dataSheetSize);
				dataSheetReadSize = PasReadSensorDataSheet(handle, device, j, dataSheetBuffer, dataSheetSize);
				if(dataSheetReadSize < 0){
					printf("Device # %d, Channel %d, couldn't read datasheet, error: %d\n",device,j,dataSheetReadSize);
					return;
				}
				
				printf("       DataSheet:\n");
				DumpBuffer(dataSheetBuffer, dataSheetReadSize);
			}
			
			// check which measurements are valid which indicates if the external sensor is plugged in
			// this requires parsing the datasheet which currently isn't exposed
			// we could add a method for returning the number of measurements
		}

	}

}



static void test_read_data(int handle, int ntimes)
{

	int readlen = -1;
	char readbuf[READSIZ];

	int devarr[DEVARRSIZ];
	int device = -1;

	int samplesize = -1;
	int nchan = -1;
	int ndev = -1;
	int i = 0, j=0;

	ndev = PasGetDevices(handle, devarr, DEVARRSIZ);

	if(ndev<0)
	{
		printf("test_read_data Error ndev = %d\n",ndev);
		return;
	}

	while(0<ntimes--)
		for(i=0; i < ndev ; i++)
		{
			printf("Reading one shot data from dev %d\n", i);
			device = devarr[i];
			nchan = PasGetNumChannels(handle,device);

			for(j=0;j<nchan;j++)
			{
				printf("Reading one shot data from dev %d channel %d\n", i, j);
				if(1!=PasGetSensorDetected(handle,device,j))
					continue;

				printf("Found sensor on channel\n");
				samplesize  = PasGetSampleSize(handle, device, j);
				printf("Device # %d, Channel %d, sample size: %d\n",device, j, samplesize);

				readbuf[0] = 0xFF;

				readlen = PasGetOneSample(handle, device, j,readbuf,READSIZ);
				if(readlen<0)
				{
					printf("Device # %d, Channel %d, error: %d \n",device,j,readlen);
					continue;
				}
				else
				{
					printf("Read %d bytes: ", readlen);
					DumpBuffer(readbuf,readlen);
				}

			}

		}
		printf("\n");
}


static void test_read_continuous_data(int handle, int ntimes)
{

	int readlen = -1;
	char readbuf[READSIZ];

	int devarr[DEVARRSIZ];
	int device = -1;

	int ratemin = -1, ratedef = -1, ratemax = -1, rate=-1;
	int total_bytes_read = 0;
	int samplesize = -1;

	int nchan = -1;
	int ndev = -1;
	int i = 0, j=0;

	int sample_period = 100;

	int prevdot = 0;

	ndev = PasGetDevices(handle, devarr, DEVARRSIZ);

	if(ndev<0)
	{
		printf("\n Number of devices unknown. Not doing read continuous test. Error ndev = %d\n",ndev);
		return;
	}

	while(0<ntimes--)
		for(i=0; i < ndev ; i++)
		{
			device = devarr[i];

			nchan = PasGetNumChannels(handle,device);

			for(j=0;j<nchan;j++)
			{
				if(1 != PasGetSensorDetected(handle,device,j))
					continue;


				/**
				Print info about the sensor
				*/
				{

					char strbuf[STRSIZ];
					strbuf[0] = 0x00;
					PasGetName(handle, device, j,strbuf,STRSIZ);
					printf("\n\nStart sampling, Device # %d, Channel %d, name: \"%s\"\n",device, j, strbuf);

				}

				/**
				Sample rate
				*/
				readbuf[0] = 0xFF;
				ratemin = PasGetSampleRateMinimum(handle, device, j);
			    ratedef = PasGetSampleRateDefault(handle, device, j);
			    ratemax = PasGetSampleRateMaximum(handle, device, j);

				if(ratemin==-1||ratedef==-1||ratemax==-1)
				{
					printf("Could not read rates, device %d, channel %d, min %d, default %d, max %d (this is OK if no HW plugged in to the port)\n", device, j,ratemin,ratedef,ratemax);
					continue;
				}
				else {
					// convert to millisecond period
					ratemin = pasco_rate_ms(ratemin);
					ratedef = pasco_rate_ms(ratedef);
					ratemax = pasco_rate_ms(ratemax);
					
					printf("Data rates: min %dms, default %dms, max %dms\n", ratemin, ratedef,ratemax);
				}

				sample_period = ratedef; /*set the rate to be the default*/
				printf( "Set Sample Period %d ms (%d Hz)\n", sample_period, (1000/sample_period) );


				
				/*
				***********************************************************************************
				Read the data.
				NOTE: You can only call PasGetSampleData() in the loop
				      Other API calls may cause failures, and possibly leave HW in a bad state.
					  //Also Ctrl-C in the middle of the loop may do the same.
					  				
				IE:
				PasStartContinuousSampling()

					While()
						PasGetSampleData();

				PasStopContinuousSampling();

				***********************************************************************************
				*/

				
				/* Start */
				samplesize = PasStartContinuousSampling(handle, device, j, sample_period); 
				if(samplesize<=0)
				{
					printf("Could not start sampling, device %d, channel %d, sample size %d, rate %d (this is OK if no HW plugged in to the port)\n", device, j, samplesize,rate);
					continue;
				}

				total_bytes_read = 0;

				while(total_bytes_read<(512)) /* total_bytes_read: May be more than 512 +sample size -- reads are not one sample at a time*/
				{
					PasMSsleep(sample_period); /* OK to call --just a utility function */

					memset(readbuf,'z',READSIZ);

					/* Read */
					readlen = PasGetSampleData (handle,device,j, samplesize, readbuf, READSIZ);

					if(readlen>0)
					{
						if(prevdot)
						{
							printf("\n");
							prevdot = 0;
						}
						DumpBuffer(readbuf,readlen);
						total_bytes_read += readlen;
					}

					if(readlen == 0)
					{
						/* did not read any bytes */
						printf("-");
						prevdot = 1;
					}

					if(readlen <0 )
					{
						printf("Failed to read data error: %d (OK if HW removed)....bailing\n",readlen);
						break;
					}

				}

				/* Stop */
				PasStopContinuousSampling (handle, device,j);

				printf("Read %d bytes \n\n", total_bytes_read);
			}

		}

		printf("\n\n");
}



static void test_channels(int handle)
{
	int devarr[DEVARRSIZ];
	int nchan = -1;
	int ndev = -1;
	int i = 0;
	int retcode = -1;

	ndev = PasGetDevices(handle, devarr, DEVARRSIZ);

	if(ndev<0)
	{
		printf("test_channels devices Error \n",ndev);
		return;
	}

	for(i=0; i < ndev ; i++)
	{
		nchan = PasGetNumChannels(handle,devarr[i]);
		printf("Device # %d has %d Channels (round ports)\n",devarr[i], nchan);
	}

	/* Fail on purpose --use an invalid device */
	nchan = PasGetNumChannels(handle, -99);
	if(nchan>-1)
		printf("FAILED Test invalid device, Should be an Error: but is not (%d)\n", nchan);

}

int main(int argc, char* argv[])
{

	int handle = -1; 
	print_banner();




	handle = PasInit();
	PasStart(handle);


	printf("\n--------------- Detect devices -------------------------------------------\n");
	test_find_devices(handle);

	printf("\n-------------- Number of Channels ----------------------------------------\n");
	test_channels(handle);

	printf("\n-------------- Sensor attributes -----------------------------------------\n");	
	test_print_some_good_stuff_for_all_sensors(handle);

	printf("\n-------------- Read one shot data ----------------------------------------\n");
	test_read_data(handle,2); /* 2 -- do the test twice */

	printf("\n-------------- Read continous data ---------------------------------------\n");	
	test_read_continuous_data(handle,2); /* 2 -- do the test twice */
	

	PasStop(handle);
	PasDelete(handle);
	
#ifdef _WINDOWS
	// do this on windows since the cmd window might close automatically otherwise
	printf("\n>>>>Press enter to end the program<<<<\n");
	getchar();
#endif
	
	return 0;
}




/**
Helper functions that may be OS specific.
At the bottom of the file so OS dependent stuff 
does not appear in the test code.

Put all OS specific:
code  
.h <<---
#defines 
etc

Below:
*/

#ifdef _WINDOWS
#pragma warning (disable : 4996)  /*MSVC --COMPLAINS about sprintf*/
#endif


void print_banner()
{
	printf("*************************************************************\n");

#ifdef NDEBUG /* may only exit in MSVC --add if needed for other OS's*/
	printf("*    Release Build: %s%s%s\n", __DATE__,", ", __TIME__);
#else
	printf("*    Debug Build: %s%s%s\n", __DATE__,", ", __TIME__);
#endif

	printf("*\n");
	printf("*    This program tests the 'C' API with Pasco Hardware\n");
	printf("*    It may test very little if the HW is in an unknown state\n");
	printf("*    Unplug and powercycle the HW as needed\n");
	printf("*\n");
	printf("*************************************************************\n");
	printf("\n\n");

}


void print_int_arr(int* arr, int len)
{
	int i = 0;
	printf("[ ");

	for(i = 0; i<len; i++)
	{
		printf("%d, ",arr[i]);
	}
	printf(" ]\n");

}

int pasco_rate_ms(int rate)
{
	if(rate & 0x80000000){
		// rate is in seconds
		return (rate & 0x7FFFFFFF)*1000;
	} else {
		// rate is in hz
		return 1000 / rate;
	}
}

void print_pasco_rate(int rate)
{
	if(rate & 0x80000000){
		printf("%d(s)", rate & 0x7FFFFFFF);
	} else {
		printf("%d(hz)", rate);
	}
}

void DumpBuffer (const void *pvBuf, int lLen)
{
    char *pszBuf =(char *) malloc(sizeof(char)*0x100);
    unsigned char *pbBuf = (unsigned char *)pvBuf;
	int lBytes = 1;
	int nRow = 0;
	
    /* for each 16 byte row */
    for (nRow = 0; nRow <= (lLen-1)/16; nRow++)
    {
        char *pszStr = pszBuf;
		
        /* fill in the hex view of the data */
        int nCol;
        for (nCol = 0; nCol < 16; nCol+=lBytes)
        {
            switch (lBytes)
            {
				case 1: /* show as bytes */
				default:
					if (nRow*16+nCol < lLen)
						pszStr += sprintf (pszStr, "%02x ", pbBuf[nRow*16+nCol]);
					else
						pszStr += sprintf (pszStr, "   ");
					break;
					
				case 2: /* show as shorts */
					if (nRow*16+nCol < lLen)
						pszStr += sprintf (pszStr, "%04x ", *(unsigned short *)&pbBuf[nRow*16+nCol]);
					else
						pszStr += sprintf (pszStr, "     ");
					break;
					
				case 4: /* show as longs */
					if (nRow*16+nCol < lLen)
						pszStr += sprintf (pszStr, "%08x ", *(unsigned long *)&pbBuf[nRow*16+nCol]);
					else
						pszStr += sprintf (pszStr, "         ");
					break;
            }
        }
		
        /* fill in the char view of the data */
        for (nCol = 0; nCol < 16; nCol++)
        {
            if (nRow*16+nCol < lLen)
            {
                char ch = pbBuf[nRow*16+nCol];
                if (ch < 0x20 || ch >= 0x7f)
                    pszStr += sprintf (pszStr, ".");
                else
                    pszStr += sprintf (pszStr, "%c", ch);
            }
            else
                pszStr += sprintf (pszStr, " ");
        }
		
		printf( "%05x: %s\n", nRow*0x10, pszBuf);
    }
    free(pszBuf);
}
