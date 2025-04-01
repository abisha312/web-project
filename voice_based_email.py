from __future__ import print_function
import speech_recognition as sr 
import yagmail
import pyttsx3
import imaplib
import email
import traceback

recognizer=sr.Recognizer()
def speak(text,voice_type='male'):
    engine = pyttsx3.init()
    voices = engine.getProperty('voices')
    
    if (voice_type == 'male'):
        engine.setProperty('voice', voices[0].id)
    elif (voice_type == 'female'):
        engine.setProperty('voice', voices[1].id)
    else:
        engine.setProperty('voice', voices[0].id)
    rate = engine.getProperty('rate')
    engine.setProperty('rate', rate-30)
    engine.say(text)
    engine.runAndWait()

voice_type = 'male'
with sr.Microphone() as source:
    print('Do you want a Male or Female voice to communicate?')
    speak('Do you want a Male or Female voice to communicate?',voice_type)
    inp=recognizer.listen(source,1)
    try:
        i=recognizer.recognize_google(inp, language="en-US")
        print('Your message: {}'.format(i))
        if 'female' in i:
            voice_type = 'female'
        elif 'male' in i:
            voice_type = 'male'
    except Exception as ex:
        print(ex)

with sr.Microphone() as source:
    speak('Do you want to read mail or send mail...',voice_type)
    decision=recognizer.listen(source)
try:
    d=recognizer.recognize_google(decision, language="en-US")
    print('Your message: {}'.format(d))
    if 'send' in d:
        with sr.Microphone() as source:
            speak('Who do you want to send the mail to...',voice_type)
            recieve=recognizer.listen(source,1)
            speak('Clearing background noise..',voice_type)
            recognizer.adjust_for_ambient_noise (source, duration=1)
            speak('Say the subject of your mail',voice_type)
            sub=recognizer.listen(source,10)
            speak("Noted, waiting for your message...",voice_type)
            recordedaudio=recognizer.listen(source,10)
            speak('Done recording..!',voice_type)
        try:
            print('Printing the message..')
            t=recognizer.recognize_google(sub, language="en-US")
            loop='no'
            print('Your mail subject: {}'.format(t))
            text=recognizer.recognize_google(recordedaudio, language="en-US")
            print('Your message: {}'.format(text))
            r=recognizer.recognize_google(recieve, language="en-US").lower()
            if 'priya' in r:
                reciever='PERSON1_WHOM_YOU_MAIL_FREQUENTLY'
            elif 'vaishnavi' in r:
                reciever='PERSON2_WHOM_YOU_MAIL_FREQUENTLY'
            else:
                pass
            print('Message has been sent to... {}'.format(r))
        except Exception as ex:
            print(ex)

        
        message=text+'\nWith Regards,\nAbisha'
        m=t
        sender=yagmail.SMTP('abishaeunice123@gmail.com','jntk zpch kquo pasc')
        sender.send(to=reciever,subject=m,contents=message)
        print('Your message has been sent successfully')
        speak('Your message has been sent successfully',voice_type)
    elif 'read' in d:
        FROM_EMAIL = 'ENTER_YOUR_EMAIL_ID'
        FROM_PWD='ENTER_YOUR_PASSWORD'
        SMTP_SERVER = "imap.gmail.com"
        SMTP_PORT = 993

        def read_email_from_gmail():
            try:
                mail = imaplib. IMAP4_SSL(SMTP_SERVER,SMTP_PORT)
                mail.login(FROM_EMAIL,FROM_PWD) 
                msgs=[]
                mail.select('inbox')
                f=1

                print('Do you want to read the recent messages or from an individual?')
                speak('Do you want to read the recent messages or from an individual?',voice_type)
                with sr.Microphone() as source:
                    recognizer.adjust_for_ambient_noise (source, duration=1)
                    read=recognizer.listen(source,1)
                re=recognizer.recognize_google(read, language="en-US").lower()
                print('You said: {}'.format(re))
                if 'recent' in re:
                    _,data=mail.search(None,'ALL')
                elif 'individual' in re:
                    f=0
                    print('Whos mail do you want me to read?...')
                    speak('Whos mail do you want me to read?...',voice_type)
                    with sr.Microphone() as source:
                        recognizer.adjust_for_ambient_noise (source, duration=1)
                        recieve=recognizer.listen(source)
                    r=recognizer.recognize_google(recieve, language="en-US").lower()
                    if 'priya' in r:
                        reciever='PERSON1_WHOM_YOU_MAIL_FREQUENTLY'
                    elif 'vaishnavi' in r:
                        reciever='PERSON2_WHOM_YOU_MAIL_FREQUENTLY'
                    else:
                        reciever='JUST FOR IMPLEMENTATION; REPLACE WITH TRY-CATCH'
                    _,data=mail.search(None,'FROM',reciever)
                 
                mail_ids= data[0].split()
                #id_list = mail_ids[0].split()
                #first_email_id = int(id_list[0])
                #latest_email_id = int(id_list[-1])
                count=0

                for num in mail_ids:
                    typ,data = mail.fetch(num, '(RFC822)')
                    msgs.append(data)

                for i in range(len(msgs)-1,0,-1):
                    count+=1
                    if count>3:
                        break
                    for response_part in msgs[i]:
                        if type(response_part) is tuple:
                            my_msg=email.message_from_bytes((response_part[1]))
                            email_subject = my_msg['subject']
                            email_from = my_msg['from']
                            date=my_msg['Date']
                            for part in my_msg.walk():
                                if part.get_content_type() == 'text/plain':
                                    email_body=str(part.get_payload())
                            print('___________________________________________________')
                            print('Date :' + str(date) + '\n')
                            print('From :' + str(email_from) + '\n')
                            if f==0:
                                speak('Should I reed this message from'+email_from,voice_type)
                            else:
                                speak('You have recieved an email from'+email_from+'Should I reed?',voice_type)
                            with sr.Microphone() as source:
                                recognizer.adjust_for_ambient_noise (source, duration=1)
                                hear=recognizer.listen(source,1)
                            try:
                                h=recognizer.recognize_google(hear, language="en-US").lower()
                                print('You said: {}'.format(h))
                                if 'yes' in h:
                                    print('Subject :' + str(email_subject) + '\n')
                                    speak('The subject is'+str(email_subject),voice_type)
                                    print('Body :' + email_body + '\n')
                                    speak('The message is:'+email_body,voice_type)
                            except Exception as ex:
                                print(ex)

                '''for (index,i) in enumerate (range (latest_email_id,first_email_id, -1)):
                    data = mail.fetch(str(i), '(RFC822)')
                    for response_part in data:
                        arr = response_part[0]
                        if isinstance(arr, tuple):
                            msg = email.message_from_string (str (arr[1],'utf-8'))
                            email_subject = msg['subject']
                            email_from = msg['from']
                            date=msg['Date']
                            if type(response_part) is tuple:
                            email_body=email.message_from_bytes((response_part[1]))
                            for part in email_body.walk():
                                if part.get_content_type()=='text/plain':
                                    message.append(part.get_payload())

                            print("date: ", date,'\n')
                            print(index+1,'From: ' + email_from + '\n')
                            speak('You have recieved an email from'+email_from+'Should I reed?',voice_type)
                            with sr.Microphone() as source:
                                recognizer.adjust_for_ambient_noise (source, duration=1)
                                hear=recognizer.listen(source)
                            try:
                                h=recognizer.recognize_google(hear, language="en-US").lower()
                                print('You said: {}'.format(h))
                                if 'yes' in h:
                                    print('Subject :' + str(email_subject) + '\n')
                                    speak('The subject is'+str(email_subject),voice_type)
                                    #print('Message :' + str(message) + '\n')
                                    #speak('The message is'+str(message),voice_type)
                                    print("-"*100)
                            except Exception as ex:
                                print(ex)
                    if index==2:
                        break'''
            except Exception as e:
                traceback.print_exc()
                print(str(e))
        read_email_from_gmail()
except Exception as ex:
    print(ex)
