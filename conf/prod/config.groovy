appdir {
	root = '.'
	covers = 'covers'
	db = 'db'
	log = 'log'
	webapp = 'web'
	resources = 'resources' // this is only needed for the nocover.svg file
}

log {
	configfile = 'conf/logback.groovy'
}

h2 {
	driver = 'org.h2.Driver'
	url = 'jdbc:h2:file:' + appdir.root + '/' + appdir.db + '/music'
	username = 'SA'
	password = ''
	showSql = true
}

server {
	port = 8000
	resource.base = appdir.webapp
}

coverart {
	nocover = 'no-cover.svg'
}