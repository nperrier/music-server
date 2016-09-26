appdir {
	root = '.'
	covers = 'covers'
	db = 'db'
	log = 'log'
	webapp = 'web'
	resources = 'resources' // this is only needed for the nocover.svg file
}

log {
	configfile = appdir.root + '/conf/logback.groovy'
}

h2 {
	driver = 'org.h2.Driver'
	url = 'jdbc:h2:file:' + appdir.root + '/' + appdir.db + '/music;DB_CLOSE_ON_EXIT=FALSE'
	username = 'SA'
	password = ''
	schemas = ['PUBLIC', 'FT']
	showSql = false
}

server {
	port = 8000
	resource.base = appdir.webapp
	mimetypes = [
			"image/svg+xml svg",
			"image/png png"
	]
}

coverart {
	nocover = 'no-cover.svg'
}

auth {
	admin {
		username = 'admin'
		password = 'changeme'
	}
	secretkey = 'changeme'
	disabled = false
}
