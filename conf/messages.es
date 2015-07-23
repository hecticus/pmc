# Override default Play's validation messages

# --- Constraints
constraint.required=Obligatorio
constraint.min=Valor mínimo: {0}
constraint.max=Valor máximo: {0}
constraint.minLength=Longitud mínima: {0}
constraint.maxLength=Longitud máxima: {0}
constraint.email=Email

# --- Formats
format.date=Date (''{0}'')
format.numeric=Numérico
format.real=Real

# --- Errors
error.invalid=Valor incorrecto
error.required=Este campo es obligatorio
error.number=Se esperaba un valor numérico
error.real=Se esperaba un numero real
error.min=Debe ser mayor o igual que {0}
error.max=Debe ser menor o igual que {0}
error.minLength=La longitud mínima es de {0}
error.maxLength=La longitud máxima es de {0}
error.email=Se requiere un email válido
error.pattern=Debe satisfacer {0}

### --- play-authenticate START

# play-authenticate: Initial translations

playauthenticate.accounts.link.success=Cuenta enlazada correctamente
playauthenticate.accounts.merge.success=Cuentas unificadas correctamente

playauthenticate.verify_email.error.already_validated=Su email ya ha sido validado
playauthenticate.verify_email.error.set_email_first=Primero debe dar de alta un email.
playauthenticate.verify_email.message.instructions_sent=Las instrucciones para validar su cuenta han sido enviadas a {0}.
playauthenticate.verify_email.success=La dirección de email ({0}) ha sido verificada correctamente.

playauthenticate.reset_password.message.instructions_sent=Las instrucciones para restablecer su contraseña han sido enviadas a {0}.
playauthenticate.reset_password.message.email_not_verified=Su cuenta aún no ha sido validada. Se ha enviado un email incluyedo instrucciones para su validación. Intente restablecer la contraseña una vez lo haya recibido.
playauthenticate.reset_password.message.no_password_account=Su usuario todavía no ha sido configurado para utilizar contraseña.
playauthenticate.reset_password.message.success.auto_login=Su contraseña ha sido restablecida.
playauthenticate.reset_password.message.success.manual_login=Su contraseña ha sido restablecida. Intente volver a entrar utilizando su nueva contraseña.

playauthenticate.change_password.error.passwords_not_same=Las contraseñas no coinciden.
playauthenticate.change_password.success=La contraseña ha sido cambiada correctamente.

playauthenticate.password.signup.error.passwords_not_same=Las contraseñas no coinciden.
playauthenticate.password.login.unknown_user_or_pw=Usuario o contraseña incorrectos.
playauthenticate.email.signup.error.not_valid=Su cuenta de correo no es valida en este sitio

playauthenticate.password.verify_signup.subject=PMC: Complete su registro
playauthenticate.password.verify_email.subject=PMC: Confirme su dirección de email
playauthenticate.password.reset_email.subject=PMC: Cómo restablecer su contraseña

# play-authenticate: Additional translations

playauthenticate.login.email.placeholder=Su dirección de email
playauthenticate.login.password.placeholder=Elija una contraseña
playauthenticate.login.password.repeat=Repita la contraseña elegida
playauthenticate.login.title=Entrar
playauthenticate.login.password.placeholder=Contraseña
playauthenticate.login.now=Entrar
playauthenticate.login.forgot.password=¿Olvidó su contraseña?
playauthenticate.login.oauth=entre usando su cuenta con alguno de los siguientes proveedores:

playauthenticate.signup.title=Registrarse
playauthenticate.signup.name=Su nombre
playauthenticate.signup.now=Regístrese
playauthenticate.signup.oauth=regístrese usando su cuenta con alguno de los siguientes proveedores:

playauthenticate.verify.account.title=Es necesario validar su email
playauthenticate.verify.account.before=Antes de configurar una contraseña
playauthenticate.verify.account.first=valide su email

playauthenticate.change.password.title=Cambio de contraseña
playauthenticate.change.password.cta=Cambiar mi contraseña

playauthenticate.merge.accounts.title=Unir cuentas
playauthenticate.merge.accounts.question=¿Desea unir su cuenta ({0}) con su otra cuenta: {1}?
playauthenticate.merge.accounts.true=Sí, ¡une estas dos cuentas!
playauthenticate.merge.accounts.false=No, quiero abandonar esta sesión y entrar como otro usuario.
playauthenticate.merge.accounts.ok=OK

playauthenticate.link.account.title=Enlazar cuenta
playauthenticate.link.account.question=¿Enlazar ({0}) con su usuario?
playauthenticate.link.account.true=Sí, ¡enlaza esta cuenta!
playauthenticate.link.account.false=No, salir y crear un nuevo usuario con esta cuenta
playauthenticate.link.account.ok=OK

# play-authenticate: Signup folder translations

playauthenticate.verify.email.title=Verifique su email
playauthenticate.verify.email.requirement=Antes de usar PMC, debe validar su email.
playauthenticate.verify.email.cta=Se le ha enviado un email a la dirección registrada. Por favor, siga el link de este email para activar su cuenta.
playauthenticate.password.reset.title=Restablecer contraseña
playauthenticate.password.reset.cta=Restablecer mi contraseña

playauthenticate.password.forgot.title=Contraseña olvidada
playauthenticate.password.forgot.cta=Enviar instrucciones para restablecer la contraseña

playauthenticate.oauth.access.denied.title=Acceso denegado por OAuth
playauthenticate.oauth.access.denied.explanation=Si quiere usar PMC con OAuth, debe aceptar la conexión.
playauthenticate.oauth.access.denied.alternative=Si prefiere no hacerlo, puede también
playauthenticate.oauth.access.denied.alternative.cta=registrarse con un usuario y una contraseña.

playauthenticate.token.error.title=Error de token
playauthenticate.token.error.message=El token ha caducado o no existe.

playauthenticate.user.exists.title=El usuario existe
playauthenticate.user.exists.message=Otro usario ya está dado de alta con este identificador.

# play-authenticate: Navigation
playauthenticate.navigation.profile=Perfil
playauthenticate.navigation.link_more=Enlazar más proveedores
playauthenticate.navigation.logout=Salir
playauthenticate.navigation.login=Entrar
playauthenticate.navigation.home=Inicio
playauthenticate.navigation.restricted=Página restringida
playauthenticate.navigation.signup=Dárse de alta

# play-authenticate: Handler
playauthenticate.handler.loginfirst=Para ver ''{0}'', debe darse primero de alta.

# play-authenticate: Profile
playauthenticate.profile.title=Perfil de usuario
playauthenticate.profile.mail=Su nombre es {0} y su dirección de mail es {1}!
playauthenticate.profile.unverified=sin validar - haga click para validar
playauthenticate.profile.verified=validada
playauthenticate.profile.providers_many=Hay {0} proveedores enlazados con su cuenta:
playauthenticate.profile.providers_one = Hay un proveedor enlazado con su cuenta:
playauthenticate.profile.logged=Ha entrado con:
playauthenticate.profile.session=Su ID de usuario es {0}. Su sesión expirará el {1}.
playauthenticate.profile.session_endless=Su ID de usuario es {0}. Su sesión no expirará nunca porque no tiene caducidad.
playauthenticate.profile.password_change=Cambie/establezca una contraseña para su cuenta

# play-authenticate - sample: Index page
playauthenticate.index.title=Bienvenido PMC
playauthenticate.index.intro=Aplicación de ejemplo de PMC
playauthenticate.index.intro_2=Esto es una plantilla para una sencilla aplicación con autentificación y autorización
playauthenticate.index.intro_3=Mire la barra de navegación superior para ver ejemplos sencillos incluyendo las características soportadas de autentificación.
playauthenticate.index.heading=Cabecera
playauthenticate.index.details=Ver detalles

# play-authenticate - sample: Restricted page
playauthenticate.restricted.secrets=¡Secretos y más secretos!

### --- play-authenticate END

main.start=Inicio
main.admin=Administracion
main.configurations=Configuraciones
main.instances=Instancias
main.applications=Aplicaciones
main.push=Enviar Notificacion
main.users=Usuarios

main.list=Listar
main.operations=Operaciones
main.create=Crear

generic.error.title=Error
generic.error.content=Revise los errores y haga la solicitud de nuevo
generic.error.forbidden=Usted no tiene acceso a esta utilidad
generic.cancel=Cancelar
generic.list.done=Hecho!
generic.list.apply=Aplicar
generic.list.empty=No hay nada que mostrar
generic.list.previous=Anterior
generic.list.next=Siguiente
generic.list.listing=Listando del
generic.list.through=al
generic.list.of=de

configs.list.head=Configuraciones
configs.list.title={0,choice,0#No hay confguraciones|1#Una configuracion encontrada|1<{0,number,integer} configuraciones encontradas}
configs.list.filter.name=Filtrar por nombre de la configuracion...
configs.list.new=Agregar nueva configuracion

configs.create=Crear configuracion
configs.edit=Editar configuracion
configs.info=Informacion de la configuracion

configs.key=Clave
configs.key.help=String para buscar la configuracion

configs.value=Valor
configs.value.help=Valor de la configuracion

configs.description=Descripcion
configs.description.help=utilidad de la configuracion

configs.submit.create=Crear esta configuracion
configs.submit.update=Actualizar esta configuracion
configs.submit.delete=Eliminar esta configuracion

configs.java.created= La configuracion {0} ha sido creada!
configs.java.updated= La configuracion {0} ha sido actualizada!
configs.java.deleted= La configuracion {0} ha sido eliminada!

instances.list.head=Instancias
instances.list.title={0,choice,0#No hay instancias|1#Una instancia encontrada|1<{0,number,integer} instancias encontradas}
instances.list.filter.name=Filtrar por nombre de la instancia...
instances.list.new=Agregar nueva instancia

instances.create=Crear instancia
instances.edit=Editar instancia
instances.info=Informacion de la instancia

instances.ip=Direccion IP
instances.ip.help=IP del servidor

instances.name=Nombre
instances.name.help=Nombre de la instancia

instances.running=Activa
instances.running.help=estado de la instancia

instances.test=Prueba
instances.test.help=mode de prueba

instances.submit.create=Crear esta instancia
instances.submit.update=Actualizar esta instancia
instances.submit.delete=Eliminar esta instancia

instances.java.created= La instancia {0} ha sido creada!
instances.java.updated= La instancia {0} ha sido actualizada!
instances.java.deleted= La instancia {0} ha sido eliminada!

applications.list.head=Aplicaciones
applications.list.title={0,choice,0#No hay aplicaciones|1#Una aplicacion encontrada|1<{0,number,integer} aplicacion encontradas}
applications.list.filter.name=Filtrar por nombre de la aplicacion...
applications.list.new=Agregar nueva aplicacion

applications.create=Crear aplicacion
applications.edit=Editar aplicacion
applications.info=Informacion de la aplicacion

applications.name=Nombre
applications.name.help=Nombre de la aplicacion

applications.active=Activa
applications.active.help=Estatus de la aplicacion 0 or 1

applications.batchClientsUrl=URL de clientes en batch
applications.batchClientsUrl.help=url del servicio que entrega los clientes en batch

applications.singleClientUrl=URL de cliente
applications.singleClientUrl.help=url del servicio que entrega un cliente

applications.cleanDeviceUrl=URL de limpieza
applications.cleanDeviceUrl.help=url del servicio que limpia los dispositivos

applications.title=Titulo
applications.title.help=Titulo que se usara en la notificacion

applications.sound=Sonido
applications.sound.help=Sonido que se reproducira en la notificacion

applications.debug=Depuracion
applications.debug.help=modo de depuracion

applications.ios=Seccion IOS
applications.iosPushApnsCertProduction=Certificado IOS de Produccion
applications.iosPushApnsCertProduction.help=Archivo que permite hacer push a versiones de produccion

applications.iosPushApnsCertSandbox=Certificado IOS de Sandbox
applications.iosPushApnsCertSandbox.help=Archivo que permite hacer push a versiones de sandbox

applications.iosPushApnsPassphrase=Contraseña IOS
applications.iosPushApnsPassphrase.help=contraseña utilizada al generar los certificados

applications.iosSandbox=IOS Sandbox
applications.iosSandbox.help=usar sandbox? 0 or 1

applications.android=Seccion Android
applications.googleApiKey=Google API Key
applications.googleApiKey.help=API key generada por Google para esta aplicacion

applications.mailgun=Seccion MailGun
applications.mailgunApikey=MailGun API Key
applications.mailgunApikey.help=API key generada por MailGun para esta aplicacion

applications.mailgunApiurl=MailGun API URL
applications.mailgunApiurl.help=URL generada por MailGun para esta aplicacion

applications.mailgunFrom=MailGun From
applications.mailgunFrom.help=Direccion a ser utilizada como remitente

applications.mailgunTo=MailGun To
applications.mailgunTo.help=Direccion a ser utilizada como destinatario (recibira cada uno de los correos)

applications.devices=Dispositivos
applications.device=Dispositivo
applications.device.add=Agregar Dispositivo
applications.device.remove=Eliminar Dispositivo
applications.device.active=Activo

applications.submit.create=Crear esta aplicacion
applications.submit.update=Actualizar esta aplicacion
applications.submit.delete=Eliminar esta aplicacion

applications.java.invalidIosProductionFile=Extension de archivo invalida para Certificado IOS de Produccion
applications.java.invalidIosSandboxFile=Extension de archivo invalida para Certificado IOS de Sandbox
applications.java.fileError=Error con archivo
applications.java.fileError.iosProduction=Falta el archivo para Certificado IOS de Produccion
applications.java.fileError.iosSandbox=Falta el archivo para Certificado IOS de Sandbox

applications.pushed=Eventos Enviados
applications.pushed.list.head=Eventos Enviados
applications.pushed.list.title={0,choice,0#No hay eventos enviados|1#Un evento enviado encontrado|1<{0,number,integer} Eventos Enviados Encontrados}
applications.pushed.list.filter.name=Filtrar por nombre de la aplicacion...
applications.pushed.message=Mensaje
applications.pushed.date=Fecha
applications.pushed.quantity=Cantidad

applications.java.created= La aplicacion {0} ha sido creada!
applications.java.updated= La aplicacion {0} ha sido actualizada!
applications.java.deleted= La aplicacion {0} ha sido eliminada!

events.create=Crear Notificacion por Push
events.info=Informacion de la Notificacion
events.application=Aplicacion
events.application.help=Aplicacion que recibira la notificacion
events.message=Mensaje
events.message.help=Mensaje a ser enviado
events.extraParams=Parametros
events.extraParams.help=Objeto JSON que define el comportamiento de la notificacion
events.all=Todos los Usuarios
events.all.help=Notificacion es masiva
events.type=Tipo
events.type.help=Tipo de dispositivo al que se enviara la notificacion
events.receivers=Receptores
events.receivers.help=Lista separada por coma de los Registration IDs que seran notificados
events.push=Enviar

users.list.head=Usuarios
users.list.title={0,choice,0#No hay usuarios|1#Un usuario encontrado|1<{0,number,integer} usuarios encontrados}
users.list.filter.name=Filtrar por nombre del usuario...
users.list.new=Agregar nuevo usuario

users.create=Crear usuario
users.edit=Editar usuario
users.info=Informacion del usuario

users.active=Activo
users.active.help=Estatus de este usuario

users.name=Nombre de usuario
users.name.help=Nombre de usuario

users.email=Correo
users.email.help=correo a ser usado como login

users.firstName=Nombre
users.firstName.help=nombre de este usuario

users.lastName=Apellido
users.lastName.help=Apellido de este usuario

users.role=Rol
users.role.add=Agregar Rol
users.role.remove=Eliminar Rol

users.submit.create=Crear este usuario
users.submit.update=Actualizar este usuario
users.submit.delete=Eliminar este usuario

users.java.created= El usuario {0} ha sido creado!
users.java.updated= El usuario {0} ha sido creado!
users.java.deleted= El usuario {0} ha sido creado!
events.java.pushed=La Notificacion {0} ha sido enviada\!
applications.device.pusher=Pusher
applications.device.resolver=Resolver
devices.list.head=Dispositivos
devices.list.title={0,choice,0\#No hay dispositivos|1\#Un dispositivo encontrado|1<{0,number,integer} dispositivos Encontrados}
devices.list.filter.name=Filtrar por nombre del dispositivo...
devices.list.new=Agregar nuevo Dispositivo
devices.create=Crear Dispositivo
devices.edit=Editar Dispositivo
devices.info=Informacion del Dispositivo
devices.name=Nombre
devices.name.help=Nombre del Dispositivo
devices.submit.create=Crear este Dispositivo
devices.submit.update=Actualizar este Dispositivo
devices.submit.delete=Eliminar este Dispositivo
devices.java.created=El dispositivo {0} ha sido creado\!
devices.java.updated=El dispositivo {0} ha sido actualizado\!
devices.java.deleted=El dispositivo {0} ha sido eliminado\!
resolvers.java.created=El resolver {0} ha sido creado\!
resolvers.java.updated=El resolver {0} ha sido actualizado\!
resolvers.java.deleted=El resolver {0} ha sido eliminado\!
pushers.java.created=El pusher {0} ha sido creado\!
pushers.java.updated=El pusher {0} ha sido actualizado\!
pushers.java.deleted=El pusher {0} ha sido eliminado\!
resolvers.list.head=Resolvers
resolvers.list.title={0,choice,0\#No hay resolvers|1\#Un resolver encontrado|1<{0,number,integer} resolvers Encontrados}
resolvers.list.filter.name=Filtrar por nombre del resolver...
resolvers.list.new=Agregar nuevo Resolver
resolvers.create=Crear Resolver
resolvers.edit=Editar Resolver
resolvers.info=Informacion del Resolver
resolvers.name=Nombre
resolvers.name.help=Nombre del Resolver
resolvers.className=Nombre de la Clase
resolvers.className.help=Nombre de la Clase del Resolver
resolvers.device=Dispositivo
resolvers.submit.create=Crear este Resolver
resolvers.submit.update=Actualizar este Resolver
resolvers.submit.delete=Eliminar este Resolver
pushers.list.head=Pushers
pushers.list.title={0,choice,0\#No hay pushers|1\#Un pusher encontrado|1<{0,number,integer} pushers Encontrados}
pushers.list.filter.name=Filtrar por nombre del pusher...
pushers.list.new=Agregar nuevo Pusher
pushers.create=Crear Pusher
pushers.edit=Editar Pusher
pushers.info=Informacion del Pusher
pushers.name=Nombre
pushers.name.help=Nombre del Pusher
pushers.className=Nombre de la Clase
pushers.className.help=Nombre de la Clase del Pusher
pushers.params=Parametros
pushers.params.help=Json de parametros del Pusher
pushers.device=Dispositivo
pushers.submit.create=Crear este Pusher
pushers.submit.update=Actualizar este Pusher
pushers.submit.delete=Eliminar este Pusher
main.devices=Dispositivos
main.resolvers=Resolvers
main.pushers=Pushers
cleaners.list.head=Cleaners
cleaners.list.title={0,choice,0\#No hay cleaners|1\#Un cleaner encontrado|1<{0,number,integer} cleaners Encontrados}
cleaners.list.filter.name=Filtrar por nombre del cleaner...
cleaners.list.new=Agregar nuevo Cleaner
cleaners.create=Crear Cleaner
cleaners.edit=Editar Cleaner
cleaners.info=Informacion del Cleaner
cleaners.name=Nombre
cleaners.name.help=Nombre del cleaner
cleaners.className=Nombre de la Clase
cleaners.className.help=Nombre de la Clase del cleaner
cleaners.device=Dispositivo
cleaners.submit.create=Crear este Cleaner
cleaners.submit.update=Actualizar este Cleaner
cleaners.submit.delete=Eliminar este Cleaner
cleaners.java.created=El cleaner {0} ha sido creado\!
cleaners.java.updated=El cleaner {0} ha sido actualizado\!
cleaners.java.deleted=El cleaner {0} ha sido eliminado\!
main.cleaners=Cleaners
applications.device.cleaner=Cleaner
