module.exports = {
    info: (NODE_ENV, SOURCE_MAP, API_ROOT) => {
        console.log(`
Build started with following configuration:
===========================================
→ NODE_ENV: ${NODE_ENV}
→ SOURCE_MAP: ${SOURCE_MAP}
→ API_ROOT: ${API_ROOT}
`);
    }
};

