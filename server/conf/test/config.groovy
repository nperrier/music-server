appdir {
	root = 'test'
	covers = 'test/covers'
	log = 'test/log'
	webapp = 'web/app'
	resources = 'src/main/resources' // this is only needed for the nocover.svg file
}

log {
	configfile = 'conf/test/logback.groovy'
}

db {
	url = 'jdbc:postgresql://localhost:5432/music-test'
	username = 'sa'
	password = ''
	//schemas = ['PUBLIC', 'FT']
	showSql = false
}

server {
	port = 8080
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

aws {
	// TODO: need to create fake S3 test class
	bucket = ''
	region = ''
	accesskeyid = ''
	secretaccesskey = ''
}